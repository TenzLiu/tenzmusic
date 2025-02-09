package com.tenz.tenzmusic.ui.mine;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tenz.tenzmusic.R;
import com.tenz.tenzmusic.adapter.LocalSongListAdapter;
import com.tenz.tenzmusic.base.BaseActivity;
import com.tenz.tenzmusic.base.BaseQuickAdapter;
import com.tenz.tenzmusic.db.DBManager;
import com.tenz.tenzmusic.entity.PlaySongBean;
import com.tenz.tenzmusic.ui.home.MusicPlayActivity;
import com.tenz.tenzmusic.ui.video.VideoPlayActivity;
import com.tenz.tenzmusic.widget.music.MusicPlayBar;
import com.tenz.tenzmusic.widget.titlebar.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class LikeSongListActivity extends BaseActivity {

    @BindView(R.id.ll_container)
    LinearLayout ll_container;
    @BindView(R.id.title_bar)
    TitleBar title_bar;
    @BindView(R.id.srl_like_song_list)
    SmartRefreshLayout srl_like_song_list;
    @BindView(R.id.rv_like_song_list)
    RecyclerView rv_like_song_list;

    @BindView(R.id.music_play_bar)
    MusicPlayBar music_play_bar;

    private LocalSongListAdapter songListAdapter;
    private List<PlaySongBean> songBeanList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_like_song_list;
    }

    @Override
    protected void initView() {
        super.initView();
        initTitleBar(title_bar,"我的喜欢");

        initRefreshLayout();
        initRecycleView();
    }

    @Override
    protected void initData() {
        super.initData();
        initMusicPlayBar(music_play_bar,ll_container);

        srl_like_song_list.autoRefresh();
    }

    private void initRefreshLayout() {
        srl_like_song_list.setEnableLoadMore(false);
        srl_like_song_list.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getListData();
            }
        });
    }

    @Override
    protected boolean isHaveMusicPlayFoot() {
        return true;
    }

    private void initRecycleView() {
        rv_like_song_list.setLayoutManager(new LinearLayoutManager(mContext));
        songBeanList = new ArrayList<>();
        songListAdapter = new LocalSongListAdapter(mContext, R.layout.item_song_list, songBeanList, new LocalSongListAdapter.Option() {
            @Override
            public void playVideo(int position) {
                Bundle bundle = new Bundle();
                bundle.putString("mv_hash",songBeanList.get(position).getMvhash());
                startActivity(VideoPlayActivity.class,bundle);
            }

            @Override
            public void dotClick(int position) {

            }
        });
        songListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putString("hash",songBeanList.get(position).getHash());
                bundle.putString("album_id",songBeanList.get(position).getAlbum_id());
                startActivity(MusicPlayActivity.class, bundle);
                mActivity.overridePendingTransition(R.anim.anim_up,R.anim.anim_no_anim);
            }
        });
        rv_like_song_list.setAdapter(songListAdapter);
    }

    /**
     * 获取歌单歌曲列表
     */
    private void getListData(){
        List<PlaySongBean> playSongByLikeList = DBManager.newInstance().playSongDao().getPlaySongByLike(true);
        if(playSongByLikeList.size() > 0){
            songBeanList.clear();
            songBeanList.addAll(playSongByLikeList);
        }
        srl_like_song_list.finishRefresh();
        songListAdapter.notifyDataSetChanged();
    }

}
