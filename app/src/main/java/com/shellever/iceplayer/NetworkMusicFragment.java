package com.shellever.iceplayer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class NetworkMusicFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private LinearLayout mLoadingTipsLayout;
    private LinearLayout mSearchBtnContainerLayout;
    private LinearLayout mSearchInputContainerLayout;
    private EditText mSearchContentEt;
    private ImageButton mSearchActionIb;
    private ListView mNetworkMusicLv;

    private MainActivity mMainActivity;

    private NetworkMusicAdapter mNetworkMusicAdapter;
    private List<SearchResult> mSearchResultList = new ArrayList<>();

    public static NetworkMusicFragment newInstance() {
        return new NetworkMusicFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mMainActivity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_network_music, container, false);
        mLoadingTipsLayout = (LinearLayout) view.findViewById(R.id.ll_loading_tips);
        mSearchBtnContainerLayout = (LinearLayout) view.findViewById(R.id.ll_search_btn_container);
        mSearchInputContainerLayout = (LinearLayout) view.findViewById(R.id.ll_search_input_container);
        mSearchContentEt = (EditText) view.findViewById(R.id.et_search_content);
        mSearchActionIb = (ImageButton) view.findViewById(R.id.ib_search_action);
        mNetworkMusicLv = (ListView) view.findViewById(R.id.lv_network_music);

        mSearchBtnContainerLayout.setOnClickListener(this);
        mSearchActionIb.setOnClickListener(this);
        mNetworkMusicLv.setOnItemClickListener(this);

        loadNetworkMusic();

        return view;
    }

    private void loadNetworkMusic() {
        // mLoadingTipsLayout.setVisibility(View.VISIBLE);
        // 执行异步网络音乐加载任务
        new NetworkMusicLoadingTask().execute(BaseConstant.BAIDU_DAYHOT_URL);
    }

    // View.OnClickListener

    @Override
    public void onClick(View v) {

    }

    // AdapterView.OnItemClickListener
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private class NetworkMusicLoadingTask extends AsyncTask<String, Integer, Integer> {

        private static final int FLAG_FAILURE = 0;
        private static final int FLAG_SUCCESS = 1;

        @Override
        protected void onPreExecute() {
            mLoadingTipsLayout.setVisibility(View.VISIBLE);
            mNetworkMusicLv.setVisibility(View.GONE);
            mSearchResultList.clear();
        }

        @Override
        protected Integer doInBackground(String... params) {
            String url = params[0];
            try {
                // 使用Jsoup组件来发送请求得到HTML网页
                Document doc = Jsoup.connect(url).userAgent(BaseConstant.USER_AGENT).timeout(6 * 1000).get();
                Elements songTitles = doc.select("span.song-title");
                Elements authorLists = doc.select("span.author_list");
                int songTitlesSize = songTitles.size();
                int authorListsSize = authorLists.size();
                for (int i = 0; i < songTitlesSize; i++) {
                    SearchResult searchResult = new SearchResult();
                    Elements urls = songTitles.get(i).getElementsByTag("a");
                    searchResult.setUrl(urls.get(0).attr("href"));
                    searchResult.setSongName(urls.get(0).text());
                    //

                    //无法正常获得a标签 <a hidefocus="true" href="/artist/7994">周杰伦</a>
                    //Elements authorElements = authorLists.get(i).getElementsByTag("a");     // IndexOutOfBoundsException
                    //searchResult.setSingerName(authorElements.get(0).text());
                    searchResult.setSingerName(authorLists.get(i).attr("title"));

                    searchResult.setAlbum("DayHot");    // 热歌榜

                    mSearchResultList.add(searchResult);
                }
            } catch (IOException e) {
                return FLAG_FAILURE;
            }
            return FLAG_SUCCESS;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == FLAG_SUCCESS) {
                mNetworkMusicAdapter = new NetworkMusicAdapter(mMainActivity, mSearchResultList);
                mNetworkMusicLv.setAdapter(mNetworkMusicAdapter);
                mNetworkMusicLv.addFooterView(LayoutInflater.from(mMainActivity).inflate(R.layout.list_footer_view_layout, null));
            }
            mLoadingTipsLayout.setVisibility(View.GONE);
            mNetworkMusicLv.setVisibility(View.VISIBLE);
        }
    }

}
