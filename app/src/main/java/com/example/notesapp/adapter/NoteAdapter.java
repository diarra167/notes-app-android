package com.example.notesapp.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.R;
import com.example.notesapp.model.Note;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notes;
    private final OnNoteClickListener listener;

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
        void onNoteDoubleClick(Note note);
    }

    public NoteAdapter(List<Note> notes, OnNoteClickListener listener) {
        this.notes = notes;
        this.listener = listener;
    }

    public void updateNotes(List<Note> newNotes) {
        this.notes = newNotes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.bind(note, listener);
    }

    @Override
    public int getItemCount() { return notes.size(); }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvDate;
        private final ImageView imgFavorite;
        private long lastClickTime = 0;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_note_title);
            tvDate = itemView.findViewById(R.id.tv_note_date);
            imgFavorite = itemView.findViewById(R.id.img_favorite);
        }

        void bind(Note note, OnNoteClickListener listener) {
            tvTitle.setText(note.getTitle());
            tvDate.setText(note.getDate());
            itemView.setBackgroundColor(Color.parseColor("#" + note.getColor()));
            imgFavorite.setVisibility(note.isFavorite() ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(v -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime < 300) {
                    listener.onNoteDoubleClick(note);
                } else {
                    itemView.postDelayed(() -> {
                        if (System.currentTimeMillis() - lastClickTime >= 300) {
                            listener.onNoteClick(note);
                        }
                    }, 300);
                }
                lastClickTime = currentTime;
            });
        }
    }
}