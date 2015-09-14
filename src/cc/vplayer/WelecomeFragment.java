package cc.vplayer;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import cc.vplayer.adapter.FragmentAdapter;

public class WelecomeFragment extends Fragment{
	private LayoutInflater mLayoutInflater;
	private ViewPager mViewPager;
	private ArrayList<Bitmap> mFrgList;
	private ImageView mPage0,mPage1,mPage2;
	private TextView btn01;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mLayoutInflater = LayoutInflater.from(getActivity());
		getActivity().getWindow().addFlags(Window.FEATURE_NO_TITLE);
		getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//ȥ����Ϣ��
		mFrgList = new ArrayList<Bitmap>();
		mFrgList.add(BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.w02)));
		mFrgList.add(BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.w03)));
		mFrgList.add(BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.w04)));
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = mLayoutInflater.inflate(R.layout.welecom_fragment, container, false);
		
		mViewPager = (ViewPager) v.findViewById(R.id.viewPager);
		mPage0 = (ImageView) v.findViewById(R.id.page0);
		mPage1 = (ImageView) v.findViewById(R.id.page1);
		mPage2 = (ImageView) v.findViewById(R.id.page2);
		btn01 = (TextView) v.findViewById(R.id.welecom_lastest_btn);
		btn01.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(),MainActivity.class));
                getActivity().finish();
			}
		});
		mViewPager.setAdapter(new FragmentAdapter<Bitmap>(getActivity().getApplicationContext(),mFrgList));
		mViewPager.setOnPageChangeListener(new MyPageChangeListener());
		
		return v;
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	
	class MyPageChangeListener implements OnPageChangeListener{

		private int currIndex;

		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		@Override
		public void onPageSelected(int arg0) {
		 //����arg0Ϊѡ�е�View  
			  
            Animation animation = null;//������������  
            switch (arg0) {  
            case 0: //ҳ��һ             
                
                if (currIndex == arg0+1) {  
                    animation = new TranslateAnimation(arg0+1, arg0, 0, 0);//Բ���ƶ�Ч���������ӵ�ǰView�ƶ�����һ��View  
                }  
                animation.setFillAfter(true);// True:����ͼƬͣ�ڶ�������λ��  
                animation.setDuration(1300);//���ö�������ʱ��  
                mPage0.setAnimation(animation);
                mPage1.setAnimation(animation);
                mPage0.setImageDrawable(getResources().getDrawable(R.drawable.page_now));//�����һ������ҳ�棬СԲ��Ϊѡ��״̬����һ��ҳ���СԲ����δѡ��״̬��  
                mPage1.setImageDrawable(getResources().getDrawable(R.drawable.page));  
                
                break;  
            case 1: //ҳ���  
                mPage1.setImageDrawable(getResources().getDrawable(R.drawable.page_now));//��ǰView  
                mPage0.setImageDrawable(getResources().getDrawable(R.drawable.page));//��һ��View  
                mPage2.setImageDrawable(getResources().getDrawable(R.drawable.page));//��һ��View  
                if (currIndex == arg0-1) {//�����������һ��View  
                    animation = new TranslateAnimation(arg0-1, arg0, 0, 0); //Բ���ƶ�Ч���������ӵ�ǰView�ƶ�����һ��View  
  
                      
                } else if (currIndex == arg0+1) {//Բ���ƶ�Ч���������ӵ�ǰView�ƶ�����һ��View����ͬ��  
  
                    animation = new TranslateAnimation(arg0+1, arg0, 0, 0);  
                }  
                break;  
            case 2:  
                mPage2.setImageDrawable(getResources().getDrawable(R.drawable.page_now));  
                mPage1.setImageDrawable(getResources().getDrawable(R.drawable.page));  
                //button.setVisibility(View.VISIBLE);
                if (currIndex == arg0-1) {  
                    animation = new TranslateAnimation(arg0-1, arg0, 0, 0);  
                } 
                btn01.setVisibility(View.VISIBLE);
                break;  
            }  
            currIndex = arg0;//���õ�ǰView  
		}
		
	}
}
