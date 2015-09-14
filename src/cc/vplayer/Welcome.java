package cc.vplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


public class Welcome extends FragmentActivity{
	FragmentManager mFragmentManager;
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welecom_layout);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//»•µÙ–≈œ¢¿∏
		super.onCreate(arg0);
		mFragmentManager = getSupportFragmentManager();
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.replace(R.id.mainFrame, new WelecomeFragment(),"f1");
		ft.commit();
	}

	/*@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		mFragmentManager = getSupportFragmentManager();
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.replace(R.id.mainFrame, new Fragment(),"f1");
		ft.commit();
		return super.onCreateView(name, context, attrs);
	}*/

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}
	
	
}
