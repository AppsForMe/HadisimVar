package com.sinan.hadisimvar.ui.allhadiths;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.sinan.hadisimvar.R;
import com.sinan.hadisimvar.data.local.entity.Hadith;

import java.util.ArrayList;
import java.util.List;

public class AllHadithsAdapter extends RecyclerView.Adapter<AllHadithsAdapter.ViewHolder> {

    private List<Hadith> hadithList = new ArrayList<>();
    private OnItemClickListener listener;

    public void setHadithList(List<Hadith> newList) {
        if (newList == null) {
            newList = new ArrayList<>();
        }

        // DiffUtil ile liste karşılaştırması
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new HadithDiffCallback(this.hadithList, newList));
        this.hadithList = new ArrayList<>(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    public interface OnItemClickListener {
        void onItemClick(Hadith hadith);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_hadiths, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return hadithList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hadith hadith = hadithList.get(position);
        holder.tvContent.setText(hadith.getContent());
        holder.tvSource.setText(hadith.getSource());

        // Sıhhat Durumu Renk - colors.xml'den
        int colorRes;
        if (hadith.authenticity != null) {
            if ("Sahih".equalsIgnoreCase(hadith.authenticity)) {
                colorRes = R.color.authenticity_sahih;
            } else if ("Zayıf".equalsIgnoreCase(hadith.authenticity)) {
                colorRes = R.color.authenticity_weak;
            } else {
                colorRes = R.color.authenticity_other;
            }
        } else {
            colorRes = R.color.authenticity_other;
        }
        int color = holder.itemView.getContext().getResources().getColor(colorRes, null);
        holder.viewStatusIndicator.setBackgroundColor(color);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(hadith);
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent, tvSource;
        View viewStatusIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvSource = itemView.findViewById(R.id.tvSource);
            viewStatusIndicator = itemView.findViewById(R.id.viewStatusIndicator);
        }
    }

    /**
     * DiffUtil Callback - Verimli liste güncellemeleri için
     */
    private static class HadithDiffCallback extends DiffUtil.Callback {
        private final List<Hadith> oldList;
        private final List<Hadith> newList;

        public HadithDiffCallback(List<Hadith> oldList, List<Hadith> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Hadith oldItem = oldList.get(oldItemPosition);
            Hadith newItem = newList.get(newItemPosition);
            // Null-safe karşılaştırma
            boolean contentEquals = (oldItem.getContent() == null && newItem.getContent() == null) ||
                    (oldItem.getContent() != null && oldItem.getContent().equals(newItem.getContent()));
            boolean sourceEquals = (oldItem.getSource() == null && newItem.getSource() == null) ||
                    (oldItem.getSource() != null && oldItem.getSource().equals(newItem.getSource()));
            return contentEquals && sourceEquals && oldItem.isFavorite() == newItem.isFavorite();
        }
    }
}
