package com.app.khclub.news.ui.utils;

import java.util.LinkedList;
import java.util.List;

import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.news.ui.model.NewsItemModel;
import com.app.khclub.news.ui.model.NewsItemModel.BodyItem;
import com.app.khclub.news.ui.model.NewsItemModel.OperateItem;
import com.app.khclub.news.ui.model.NewsItemModel.TitleItem;
import com.app.khclub.news.ui.model.NewsModel;

/**
 * 将动态的数据分拆成不同模块的显示
 * */
public class NewsToItemData {
	// 将动态转换为动态不同item形式的数据
	public static List<NewsItemModel> newsDataToItems(
			List<NewsModel> orgDataList) {
		LinkedList<NewsItemModel> itemList = new LinkedList<NewsItemModel>();
		for (NewsModel newsMd : orgDataList) {
			itemList.add(createNewsTitle(newsMd, NewsItemModel.NEWS_TITLE));
			itemList.add(createBody(newsMd, NewsItemModel.NEWS_BODY));
			itemList.add(createOperate(newsMd, NewsItemModel.NEWS_OPERATE));
		}
		return itemList;
	}

	// 提取动态中的头部信息
	private static NewsItemModel createNewsTitle(NewsModel news, int Type) {
		TitleItem item = new TitleItem();
		try {
			item.setItemType(Type);
			item.setNewsID(news.getNewsID());
			//
			item.setUserID(news.getUid());
			item.setHeadImage(news.getUserHeadImage());
			item.setHeadSubImage(news.getUserHeadSubImage());
			item.setUserName(news.getUserName());
			item.setUserCompany(news.getUserCompany());
			item.setUserJob(news.getUserJob());
		} catch (Exception e) {
			LogUtils.e("createNewsTitle error.");
		}
		return (NewsItemModel) item;
	}

	// 提取新闻中的主体信息
	private static NewsItemModel createBody(NewsModel news, int Type) {
		BodyItem item = new BodyItem();
		try {
			item.setItemType(Type);
			item.setNewsID(news.getNewsID());
			//
			item.setNewsContent(news.getNewsContent());
			item.setImageNewsList(news.getImageNewsList());
			item.setLocation(news.getLocation());
		} catch (Exception e) {
			LogUtils.e("createBody error.");
		}
		return (NewsItemModel) item;
	}

	// 提取新闻中的操作信息
	private static NewsItemModel createOperate(NewsModel news, int Type) {
		OperateItem item = new OperateItem();
		try {
			item.setItemType(Type);
			item.setNewsID(news.getNewsID());
			//
			item.setSendTime(news.getSendTime());
			item.setLikeCount(news.getLikeQuantity());
			item.setIsLike(news.getIsLike());
			item.setCommentCount(String.valueOf(news.getCommentQuantity()));

		} catch (Exception e) {
			LogUtils.e("createOperate error.");
		}
		return (NewsItemModel) item;
	}

}
