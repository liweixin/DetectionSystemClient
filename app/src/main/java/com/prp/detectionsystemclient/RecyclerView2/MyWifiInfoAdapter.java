package com.prp.detectionsystemclient.RecyclerView2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prp.detectionsystemclient.R;
import com.prp.detectionsystemclient.util.Util;

import java.util.List;

/**
 * Created by lwx on 2016/1/31.
 */
public class MyWifiInfoAdapter extends RecyclerView.Adapter<MyWifiInfoAdapter.ViewHolder> implements View.OnClickListener {

    private MyWifiInfoOnItemClickListener mOnItemClickListener = null;
    public void setmOnItemClickListener(MyWifiInfoOnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    public List<MyWifiInfo> datas;
    public MyWifiInfoAdapter(List<MyWifiInfo> datas){
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyWifiInfo myWifiInfo = datas.get(position);
        holder.content.setText(myWifiInfo.getContent());
        holder.itemView.setTag(myWifiInfo);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public void onClick(View v){
        if (mOnItemClickListener!=null) {
            mOnItemClickListener.onItemClick(v, (MyWifiInfo) v.getTag());
        }
    }

    public void addItem(int position, MyWifiInfo item){
        datas.add(position, item);
        notifyItemInserted(position);
    }

    public void removeItem(MyWifiInfo item){
        int position = datas.indexOf(item);
        if(position>=0) {
            datas.remove(position);
            notifyItemRemoved(position);
        } else {
            Util.toast("列表中不存在" + item.getContent() + "了哟" + "(´・∀・｀)");
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView content;
        public ViewHolder (View view){
            super(view);
            content = (TextView) view.findViewById(R.id.tv);
        }
    }
}