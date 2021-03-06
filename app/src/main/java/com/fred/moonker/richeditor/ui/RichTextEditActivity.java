package com.fred.moonker.richeditor.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.fred.moonker.MainActivity;
import com.fred.moonker.Model.Article;
import com.fred.moonker.Model.CommonResult;
import com.fred.moonker.Model.RetCode;
import com.fred.moonker.Model.User;
import com.fred.moonker.MoonkerApplication;
import com.fred.moonker.R;
import com.fred.moonker.richeditor.view.ColorPickerView;
import com.fred.moonker.richeditor.view.RichEditor;
import com.fred.moonker.tools.CacheTool;
import com.fred.moonker.tools.JsonTools;
import com.fred.moonker.tools.NetTools;
import com.fred.moonker.tools.PicTools;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class RichTextEditActivity extends Activity implements View.OnClickListener {
    private Button btnPublish;
    private EditText etTitle;
    private List<Uri>  uriList;
    private RequestQueue requestQueue;
    private Boolean isUpdate;
    private Long articleId;

    private Context context = this;
    /********************View**********************/
    //???????????????
    private RichEditor mEditor;
    //????????????
    private ImageView mBold;
    //???????????????
    private TextView mTextColor;
    //????????????View
    private LinearLayout llColorView;
    //????????????
    private TextView mPreView;
    //????????????
    private TextView mImage;
    //??????????????????ol???
    private ImageView mListOL;
    //??????????????????ul???
    private ImageView mListUL;
    //???????????????
    private ImageView mLean;
    //????????????
    private ImageView mItalic;
    //???????????????
    private ImageView mAlignLeft;
    //???????????????
    private ImageView mAlignRight;
    //??????????????????
    private ImageView mAlignCenter;
    //????????????
    private ImageView mIndent;
    //??????????????????
    private ImageView mOutdent;
    //????????????
    private ImageView mBlockquote;
    //???????????????
    private ImageView mStrikethrough;
    //????????????
    private ImageView mSuperscript;
    //????????????
    private ImageView mSubscript;
    /********************boolean??????**********************/
    //????????????
    boolean isClickBold = false;
    //????????????????????????
    boolean isAnimating = false;
    //?????????ol??????
    boolean isListOl = false;
    //?????????ul??????
    boolean isListUL = false;
    //?????????????????????
    boolean isTextLean = false;
    //?????????????????????
    boolean isItalic = false;
    //???????????????
    boolean isAlignLeft = false;
    //???????????????
    boolean isAlignRight = false;
    //???????????????
    boolean isAlignCenter = false;
    //????????????
    boolean isIndent = false;
    //??????????????????
    boolean isOutdent = false;
    //????????????
    boolean isBlockquote = false;
    //???????????????
    boolean isStrikethrough = false;
    //????????????
    boolean isSuperscript = false;
    //????????????
    boolean isSubscript = false;
    /********************??????**********************/
    //?????????????????????
    private int mFoldedViewMeasureHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rich_text_edit);

        initView();
        initClickListener();
        setPublish();
        //????????????????????????
        updateMyArticle();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void updateMyArticle() {
        isUpdate = false;
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String userNickname = intent.getStringExtra("authorName");
        articleId = intent.getLongExtra("articleId",-1L);
        Long authorId = intent.getLongExtra("authorID",-1L);
        if(articleId != -1L){
            isUpdate = true;
            etTitle.setText(title);
            getArticleContent(articleId);
            btnPublish.setText("??????");
        }else {
            return;
        }
    }

    private void getArticleContent(Long articleId) {
        CacheTool.clearAllCache(context);
        String articleIdS = String.format("%05d", articleId);
        Log.i(TAG, "getArticle: "+articleIdS);
        String url = MoonkerApplication.HTML_PATH+ "mb" + articleIdS + ".html";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        wvArticleDetail.loadDataWithBaseURL(url,response,"text/html","UTF-8","");
                        Document doc = Jsoup.parse(response);
                        Elements imgs = doc.getElementsByTag("img");
                        for (int i = 0; i < imgs.size(); i++){
                            String src = imgs.get(i).attributes().get("src");
                            if(isUpdate && src.startsWith("..")){
                                src = MoonkerApplication.ARTICLE_PIC_PATH + src.substring(2);
                                imgs.get(i).attr("src",src);
                                Log.i(TAG, "getArticleContent: "+src);
                            }
                        }
                        mEditor.setHtml(doc.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"????????????"+url, Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void setPublish(){
        uriList = new ArrayList<>();
        //
        etTitle = findViewById(R.id.rte_et_title);
        btnPublish = findViewById(R.id.tool_bar_publish);
        btnPublish.setOnClickListener(view ->{
            btnPublish.setEnabled(false);
            Toast.makeText(context,"??????????????????,?????????",Toast.LENGTH_LONG);
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            String html = mEditor.getHtml();

            Document doc = Jsoup.parse(html);
            Elements imgs = doc.getElementsByTag("img");
            uriList.clear();
            for (int i = 0; i < imgs.size(); i++){
                String src = imgs.get(i).attributes().get("src");
                if(isUpdate && src.startsWith("..")){
                    src = MoonkerApplication.ARTICLE_PIC_PATH + src.substring(2);
                    Log.i(TAG, "setPublish: "+src);
                }
                Uri uri = Uri.parse(src);
                uriList.add(uri);
            }
            String title = etTitle.getText().toString();
            long userId = ((MoonkerApplication) getApplication()).getUser().getUserID();
            //??????????????????
            Article article = new Article();
            if(isUpdate)
                article.setArticleId(articleId);
            article.setAuthorId(userId);
            article.setTitle(title);
            article.setArticleContent(html);
            article.setPics(PicTools.convertUriListToStringList(uriList,context));

            JSONObject jsonObject = JsonTools.toJsonObject(article);
            Log.i(TAG, "setPublish: "+jsonObject.toString());
            String url = MoonkerApplication.URL+MoonkerApplication.ARTICLE_PREFIX+"/publish";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i(TAG, "onResponse: " + response.toString());
                            CommonResult<Long> longCommonResult = JsonTools.toCommonResult(response, Long.class);
                            if(longCommonResult.getCode().equals(RetCode.OK)){
                                Intent intent = new Intent(context, MainActivity.class);
                                startActivity(intent);
                            }else {
                                Toast.makeText(context,longCommonResult.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                            btnPublish.setEnabled(true);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context,"????????????"+url, Toast.LENGTH_SHORT).show();
                            btnPublish.setEnabled(true);
                        }
                    });
            requestQueue.add(jsonObjectRequest);

        });
    }

    /**
     * ?????????View
     */
    private void initView() {
        initEditor();
        initMenu();
        initColorPicker();
        requestQueue = NetTools.getInstance(this.getApplicationContext()).getRequestQueue();
    }

    /**
     * ????????????????????????
     */
    private void initEditor() {
        mEditor = findViewById(R.id.re_main_editor);
        //mEditor.setEditorHeight(400);
        //??????????????????????????????
        mEditor.setEditorFontSize(18);
        //??????????????????????????????
        mEditor.setEditorFontColor(Color.BLACK);
        //?????????????????????
        mEditor.setEditorBackgroundColor(Color.WHITE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        //???????????????padding
        mEditor.setPadding(10, 10, 10, 10);
        //??????????????????
        mEditor.setPlaceholder("?????????????????????");
        //??????????????????
        //mEditor.setInputEnabled(false);
        //???????????????????????????
        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                Log.d("mEditor", "html?????????" + text);
            }
        });
    }

    /**
     * ????????????????????????
     */
    private void initColorPicker() {
        ColorPickerView right = findViewById(R.id.cpv_main_color);
        right.setOnColorPickerChangeListener(new ColorPickerView.OnColorPickerChangeListener() {
            @Override
            public void onColorChanged(ColorPickerView picker, int color) {
                mTextColor.setBackgroundColor(color);
                mEditor.setTextColor(color);
            }

            @Override
            public void onStartTrackingTouch(ColorPickerView picker) {

            }

            @Override
            public void onStopTrackingTouch(ColorPickerView picker) {

            }
        });
    }

    /**
     * ?????????????????????
     */
    private void initMenu() {
        mBold = findViewById(R.id.button_bold);
        mTextColor = findViewById(R.id.button_text_color);
        llColorView = findViewById(R.id.ll_main_color);
        mPreView = findViewById(R.id.tv_main_preview);
        mImage = findViewById(R.id.button_image);
        mListOL = findViewById(R.id.button_list_ol);
        mListUL = findViewById(R.id.button_list_ul);
        mLean = findViewById(R.id.button_underline);
        mItalic = findViewById(R.id.button_italic);
        mAlignLeft = findViewById(R.id.button_align_left);
        mAlignRight = findViewById(R.id.button_align_right);
        mAlignCenter = findViewById(R.id.button_align_center);
        mIndent = findViewById(R.id.button_indent);
        mOutdent = findViewById(R.id.button_outdent);
        mBlockquote = findViewById(R.id.action_blockquote);
        mStrikethrough = findViewById(R.id.action_strikethrough);
        mSuperscript = findViewById(R.id.action_superscript);
        mSubscript = findViewById(R.id.action_subscript);
        getViewMeasureHeight();
    }

    /**
     * ?????????????????????
     */
    private void getViewMeasureHeight() {
        //??????????????????
        float mDensity = getResources().getDisplayMetrics().density;
        //?????????????????????
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        llColorView.measure(w, h);
        int height = llColorView.getMeasuredHeight();
        mFoldedViewMeasureHeight = (int) (mDensity * height + 0.5);
    }

    private void initClickListener() {
        mBold.setOnClickListener(this);
        mTextColor.setOnClickListener(this);
        mPreView.setOnClickListener(this);
        mImage.setOnClickListener(this);
        mListOL.setOnClickListener(this);
        mListUL.setOnClickListener(this);
        mLean.setOnClickListener(this);
        mItalic.setOnClickListener(this);
        mAlignLeft.setOnClickListener(this);
        mAlignRight.setOnClickListener(this);
        mAlignCenter.setOnClickListener(this);
        mIndent.setOnClickListener(this);
        mOutdent.setOnClickListener(this);
        mBlockquote.setOnClickListener(this);
        mStrikethrough.setOnClickListener(this);
        mSuperscript.setOnClickListener(this);
        mSubscript.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button_bold:
                if (isClickBold) {
                    mBold.setImageResource(R.mipmap.bold);
                } else {  //??????
                    mBold.setImageResource(R.mipmap.bold_);
                }
                isClickBold = !isClickBold;
                mEditor.setBold();
                break;
            case R.id.button_text_color:
                //??????????????????
                //????????????????????????,??????return,????????????????????????,??????????????????????????????,
                // ??????????????????ImageButton???????????????????????????
                if (isAnimating) return;
                //????????????????????????,?????????????????????isAnimating??????true , ??????????????????????????????????????????
                //?????????,????????????????????????,??????????????????????????????isAnimating??????false,??????????????????????????????
                isAnimating = true;

                if (llColorView.getVisibility() == View.GONE) {
                    //????????????
                    animateOpen(llColorView);
                } else {
                    //????????????
                    animateClose(llColorView);
                }
                break;
            case R.id.button_image:
                //????????????
                //????????????????????????????????????????????????insertImage????????????URL???????????????????????????????????????????????????????????????????????????
                //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                //?????????????????????URL?????????????????????????????????????????????????????????????????????URL???????????????????????????????????????????????????????????????
                // ??????????????????<uses-permission android:name="android.permission.INTERNET" />???
//                mEditor.insertImage("http://www.1honeywan.com/dachshund/image/7.21/7.21_3_thumb.JPG",
//                        "dachshund");
                toPicture();
                break;
            case R.id.button_list_ol:
                if (isListOl) {
                    mListOL.setImageResource(R.mipmap.list_ol);
                } else {
                    mListOL.setImageResource(R.mipmap.list_ol_);
                }
                isListOl = !isListOl;
                mEditor.setNumbers();
                break;
            case R.id.button_list_ul:
                if (isListUL) {
                    mListUL.setImageResource(R.mipmap.list_ul);
                } else {
                    mListUL.setImageResource(R.mipmap.list_ul_);
                }
                isListUL = !isListUL;
                mEditor.setBullets();
                break;
            case R.id.button_underline:
                if (isTextLean) {
                    mLean.setImageResource(R.mipmap.underline);
                } else {
                    mLean.setImageResource(R.mipmap.underline_);
                }
                isTextLean = !isTextLean;
                mEditor.setUnderline();
                break;
            case R.id.button_italic:
                if (isItalic) {
                    mItalic.setImageResource(R.mipmap.lean);
                } else {
                    mItalic.setImageResource(R.mipmap.lean_);
                }
                isItalic = !isItalic;
                mEditor.setItalic();
                break;
            case R.id.button_align_left:
                if (isAlignLeft) {
                    mAlignLeft.setImageResource(R.mipmap.align_left);
                } else {
                    mAlignLeft.setImageResource(R.mipmap.align_left_);
                }
                isAlignLeft = !isAlignLeft;
                mEditor.setAlignLeft();
                break;
            case R.id.button_align_right:
                if (isAlignRight) {
                    mAlignRight.setImageResource(R.mipmap.align_right);
                } else {
                    mAlignRight.setImageResource(R.mipmap.align_right_);
                }
                isAlignRight = !isAlignRight;
                mEditor.setAlignRight();
                break;
            case R.id.button_align_center:
                if (isAlignCenter) {
                    mAlignCenter.setImageResource(R.mipmap.align_center);
                } else {
                    mAlignCenter.setImageResource(R.mipmap.align_center_);
                }
                isAlignCenter = !isAlignCenter;
                mEditor.setAlignCenter();
                break;
            case R.id.button_indent:
                if (isIndent) {
                    mIndent.setImageResource(R.mipmap.indent);
                } else {
                    mIndent.setImageResource(R.mipmap.indent_);
                }
                isIndent = !isIndent;
                mEditor.setIndent();
                break;
            case R.id.button_outdent:
                if (isOutdent) {
                    mOutdent.setImageResource(R.mipmap.outdent);
                } else {
                    mOutdent.setImageResource(R.mipmap.outdent_);
                }
                isOutdent = !isOutdent;
                mEditor.setOutdent();
                break;
            case R.id.action_blockquote:
                if (isBlockquote) {
                    mBlockquote.setImageResource(R.mipmap.blockquote);
                } else {
                    mBlockquote.setImageResource(R.mipmap.blockquote_);
                }
                isBlockquote = !isBlockquote;
                mEditor.setBlockquote();
                break;
            case R.id.action_strikethrough:
                if (isStrikethrough) {
                    mStrikethrough.setImageResource(R.mipmap.strikethrough);
                } else {
                    mStrikethrough.setImageResource(R.mipmap.strikethrough_);
                }
                isStrikethrough = !isStrikethrough;
                mEditor.setStrikeThrough();
                break;
            case R.id.action_superscript:
                if (isSuperscript) {
                    mSuperscript.setImageResource(R.mipmap.superscript);
                } else {
                    mSuperscript.setImageResource(R.mipmap.superscript_);
                }
                isSuperscript = !isSuperscript;
                mEditor.setSuperscript();
                break;
            case R.id.action_subscript:
                if (isSubscript) {
                    mSubscript.setImageResource(R.mipmap.subscript);
                } else {
                    mSubscript.setImageResource(R.mipmap.subscript_);
                }
                isSubscript = !isSubscript;
                mEditor.setSubscript();
                break;
            case R.id.tv_main_preview:
                Intent intent = new Intent(RichTextEditActivity.this, WebDataActivity.class);
                intent.putExtra("diarys", mEditor.getHtml());
                startActivity(intent);
                break;
        }
    }

    /**
     * ????????????
     *
     * @param view ???????????????view
     */
    private void animateOpen(LinearLayout view) {
        view.setVisibility(View.VISIBLE);
        ValueAnimator animator = createDropAnimator(view, 0, mFoldedViewMeasureHeight);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
            }
        });
        animator.start();
    }

    /**
     * ????????????
     *
     * @param view ???????????????view
     */
    private void animateClose(final LinearLayout view) {
        int origHeight = view.getHeight();
        ValueAnimator animator = createDropAnimator(view, origHeight, 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                isAnimating = false;
            }
        });
        animator.start();
    }


    /**
     * ????????????
     *
     * @param view  ????????????????????????view
     * @param start view?????????
     * @param end   view?????????
     * @return ValueAnimator??????
     */
    private ValueAnimator createDropAnimator(final View view, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = value;
                view.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    private void toPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);  //????????? ACTION_IMAGE_CAPTURE
        intent.setType("image/*");
        startActivityForResult(intent,100);
        Log.d(TAG, "toPicture: "+"??????????????????");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode != RESULT_CANCELED){
            if(data != null){
                Uri picUri = data.getData();
                mEditor.insertImage(picUri.toString(),"dachshund");
            }
        }
    }
}