package com.kekezu.kppw.ruihui;

import com.kekezu.kppw.R;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ContentFragment extends Fragment {
	private ProgressBar progressBar;
	public static ContentFragment newInstance(String msg){
		ContentFragment fragment = new ContentFragment();
		Bundle args = new Bundle();
		args.putString("msg", msg);
		fragment.setArguments(args);
		return fragment;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		
//		View view = inflater.inflate(R.layout.out_content,null);
		WebView webView = (WebView) inflater.inflate(R.layout.out_web, null);
		progressBar= (ProgressBar)webView.findViewById(R.id.progressbar);//进度条
//		TextView tvMsg=(TextView) view.findViewById(R.id.tv_msg);
		Bundle args = getArguments();
		if(args!=null){
			String msg=args.getString("msg","");
//			tvMsg.setText(msg);
			webView.loadUrl(msg);//加载url
			  webView.addJavascriptInterface(this,"android");//添加js监听 这样html就能调用客户端
		        webView.setWebChromeClient(webChromeClient);
		        webView.setWebViewClient(webViewClient);

		        WebSettings webSettings=webView.getSettings();
		        webSettings.setJavaScriptEnabled(false);//允许使用js

		        /**
		         * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
		         * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
		         * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
		         * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
		         */
		        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存，只从网络获取数据.

		        //支持屏幕缩放
		        webSettings.setSupportZoom(true);
		        webSettings.setBuiltInZoomControls(true);
	
		}
		
		return webView;
		
	}
	//WebViewClient主要帮助WebView处理各种通知、请求事件
    private WebViewClient webViewClient=new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url) {//页面加载完成
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("ansen","拦截url:"+url);
            if(url.equals("http://www.google.com/")){
            	Log.i("google", "无法访问谷歌");
//                Toast.makeText(MainActivity.this,"国内不能访问google,拦截该url",Toast.LENGTH_LONG).show();
                return true;//表示我已经处理过了
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

    };
    //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
    private WebChromeClient webChromeClient=new WebChromeClient(){
        //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
            localBuilder.setMessage(message).setPositiveButton("确定",null);
            localBuilder.setCancelable(false);
            localBuilder.create().show();

            //注意:
            //必须要这一句代码:result.confirm()表示:
            //处理结果为确定状态同时唤醒WebCore线程
            //否则不能继续点击按钮
            result.confirm();
            return true;
        }

        //获取网页标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            Log.i("ansen","网页标题:"+title);
        }

        //加载进度回调
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBar.setProgress(newProgress);
        }
    };


}
