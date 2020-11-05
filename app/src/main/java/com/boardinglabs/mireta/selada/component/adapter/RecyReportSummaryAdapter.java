package com.boardinglabs.mireta.selada.component.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.network.entities.SummaryReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dhimas on 11/28/17.
 */

public class RecyReportSummaryAdapter extends RecyclerView.Adapter<RecyReportSummaryAdapter.ViewHolder>{
    private List<SummaryReport> reportList;
    private OnClickItem onClickItem;

    public RecyReportSummaryAdapter(OnClickItem onClick) {
        reportList = new ArrayList<>();
        onClickItem = onClick;
    }

    public void setData(List<SummaryReport> list) {
        reportList = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyReportSummaryAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_report_summary, parent, false));
    }

    int getDrawableId(Context c, String imageName) {
        return c.getResources().getIdentifier(imageName, "drawable", c.getPackageName());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        SummaryReport report = reportList.get(position);
        holder.reportName.setText(report.report_name);
        holder.reportValue.setText(report.report_value);
        holder.reportPeriod.setText(report.report_period);


        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickItem != null) {
                    onClickItem.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView reportName;
        private TextView reportValue;
        private TextView reportPeriod;
        private LinearLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            reportName = (TextView) itemView.findViewById(R.id.report_name);
            reportValue = (TextView) itemView.findViewById(R.id.report_value);
            reportPeriod = (TextView) itemView.findViewById(R.id.report_period);
            container = (LinearLayout) itemView.findViewById(R.id.container);
        }
    }

    public interface OnClickItem {
        void onClick(int position);
    }
}
