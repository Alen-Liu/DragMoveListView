package com.example.mylistview;

/**
 * 单独的main 文件,  各个类型的listview 都用这一个activity 展示.
 * 所有listview 使用相同的 adpater-xml  和  主acitivity.
 * 主要实现功能:
 * 		1.下拉刷新 listview  包括下拉展示/隐藏head 和 下拉 动画head.
 * 		2. 拖动排序
 * 		3. 左右滑动项目
 * 		4. 字母快捷索引
 * 		5. 自动滚动
 * 		6. 分页加载
 * 		7. RecyclerView 动画效果
 * 		listView 的其他效果 几乎都是从这里面继承出来的.  重点关注下RecyclerView
 * */
/**
 * 1. 最基本的listview 的结构  1. 自定义listItem 的xml 文件(adapter_view.xml) ; 2. 自定义Adapter(MyListAdapter.java) ,重写getView 方法 ; 3.初始化Data 和 Adapter
 * */

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
public class MainActivity extends Activity {

	private MyListView mMyListView;
	private ArrayList<Integer> mData;
	private MyListAdapter mAdapter;
	
	//定义feature 控制开关
	private boolean mEnableRefreshHead;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// 1.3 初始化Data 和 Adapter
		mMyListView = (MyListView) findViewById(R.id.mylistview);
		initData();
		mAdapter = new MyListAdapter(this, mData);
		mMyListView.setAdapter(mAdapter);

	}

	/**
	 * For data init.
	 * */
	private void initData() {
		// TODO Auto-generated method stub
		mData = new ArrayList<Integer>();
		for(int i = 1; i < 100; i++) {
			mData.add(i);
		}
	}
}
