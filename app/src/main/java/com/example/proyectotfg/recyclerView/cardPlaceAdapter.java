package com.example.proyectotfg.recyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyectotfg.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class cardPlaceAdapter extends RecyclerView.Adapter<cardPlaceAdapter.cardPlaceViewHolder> {
    private List<cardPlace> mCardPlaceList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class cardPlaceViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public cardPlaceViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.namePlace);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public cardPlaceAdapter(List<cardPlace> cardPlaceList) {
        mCardPlaceList = cardPlaceList;
    }

    @NonNull
    @Override
    public cardPlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        cardPlaceViewHolder evh = new cardPlaceViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull cardPlaceViewHolder holder, int position) {
        cardPlace currentItem = mCardPlaceList.get(position);
        holder.mTextView.setText(currentItem.getName());
    }

    @Override
    public int getItemCount() {
        return mCardPlaceList.size();
    }
}


/*public class cardPlaceAdapter extends FirestoreRecyclerAdapter<cardPlace, cardPlaceAdapter.ViewHolder> implements View.OnClickListener {

    private OnItemClickListener onItemClickListener;

    private View.OnClickListener listener;

    public cardPlaceAdapter(@NonNull List<cardPlace> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int i, @NonNull cardPlace model) {
        holder.name.setText(model.getName());
        Glide.with(holder.itemView.getContext())
                .load(model.getImgurl()) // Usa la URL de la imagen almacenada en el modelo
                .centerInside()
                .placeholder(R.color.barf_green)
                .into(holder.imgurl);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView imgurl;
        private View.OnClickListener listener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.namePlace);
            imgurl = itemView.findViewById(R.id.imgPlace);
            // Configurar el click en el listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                        onItemClickListener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }
}*/
