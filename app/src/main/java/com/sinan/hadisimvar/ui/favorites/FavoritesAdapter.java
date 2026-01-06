package com.sinan.hadisimvar.ui.favorites;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.sinan.hadisimvar.R;
import com.sinan.hadisimvar.data.local.entity.Hadith;

import java.util.ArrayList;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private List<Hadith> hadithList = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(Hadith hadith);

        void onItemClick(Hadith hadith);
    }

    public FavoritesAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setHadithList(List<Hadith> newList) {
        if (newList == null) {
            newList = new ArrayList<>();
        }

        // DiffUtil ile liste karşılaştırması
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new FavoritesDiffCallback(this.hadithList, newList));
        this.hadithList = new ArrayList<>(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_hadith, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hadith hadith = hadithList.get(position);
        holder.tvContent.setText(hadith.getContent());
        holder.tvSource.setText(hadith.getSource());

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(hadith);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(hadith);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hadithList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent, tvSource;
        ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvSource = itemView.findViewById(R.id.tvSource);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    /**
     * DiffUtil Callback - Verimli liste güncellemeleri için
     */
    private static class FavoritesDiffCallback extends DiffUtil.Callback {
        private final List<Hadith> oldList;
        private final List<Hadith> newList;

        public FavoritesDiffCallback(List<Hadith> oldList, List<Hadith> newList) {
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
