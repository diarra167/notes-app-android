package com.example.notesapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.adapter.NoteAdapter;
import com.example.notesapp.database.NoteDao;
import com.example.notesapp.model.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnNoteClickListener {

    public static final String EXTRA_NOTE_ID = "note_id";
    public static final String EXTRA_NOTE_COLOR = "note_color";

    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private NoteDao noteDao;
    private List<Note> currentNotes = new ArrayList<>();
    private EditText etSearch;
    private Button btnFavorites;
    private TextView tvEmpty;
    private boolean showingFavorites = false;

    // Color palette FAB
    private FloatingActionButton fabAdd;
    private View fabGreen, fabRed, fabBlue, fabYellow, fabOrange, fabGray;
    private boolean isPaletteOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noteDao = new NoteDao(this);
        initViews();
        setupRecyclerView();
        setupSearch();
        setupFavoritesFilter();
        setupFab();
        loadNotes();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_notes);
        etSearch = findViewById(R.id.et_search);
        btnFavorites = findViewById(R.id.btn_favorites);
        tvEmpty = findViewById(R.id.tv_empty);
        fabAdd = findViewById(R.id.fab_add);
        fabGreen = findViewById(R.id.fab_green);
        fabRed = findViewById(R.id.fab_red);
        fabBlue = findViewById(R.id.fab_blue);
        fabYellow = findViewById(R.id.fab_yellow);
        fabOrange = findViewById(R.id.fab_orange);
        fabGray = findViewById(R.id.fab_gray);
    }

    private void setupRecyclerView() {
        adapter = new NoteAdapter(currentNotes, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterNotes(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFavoritesFilter() {
        btnFavorites.setOnClickListener(v -> {
            showingFavorites = !showingFavorites;
            btnFavorites.setSelected(showingFavorites);
            loadNotes();
        });
    }

    private void setupFab() {
        fabAdd.setOnClickListener(v -> togglePalette());
        View.OnClickListener colorClick = v -> {
            String color = (String) v.getTag();
            closePalette();
            openCreateNote(color);
        };
        fabGreen.setTag("219653"); fabGreen.setOnClickListener(colorClick);
        fabRed.setTag("EB5757");   fabRed.setOnClickListener(colorClick);
        fabBlue.setTag("2F80ED");  fabBlue.setOnClickListener(colorClick);
        fabYellow.setTag("F2C94C"); fabYellow.setOnClickListener(colorClick);
        fabOrange.setTag("F2994A"); fabOrange.setOnClickListener(colorClick);
        fabGray.setTag("828282");  fabGray.setOnClickListener(colorClick);
    }

    private void togglePalette() {
        isPaletteOpen = !isPaletteOpen;
        int visibility = isPaletteOpen ? View.VISIBLE : View.GONE;
        fabGreen.setVisibility(visibility);
        fabRed.setVisibility(visibility);
        fabBlue.setVisibility(visibility);
        fabYellow.setVisibility(visibility);
        fabOrange.setVisibility(visibility);
        fabGray.setVisibility(visibility);
    }

    private void closePalette() {
        isPaletteOpen = false;
        fabGreen.setVisibility(View.GONE);
        fabRed.setVisibility(View.GONE);
        fabBlue.setVisibility(View.GONE);
        fabYellow.setVisibility(View.GONE);
        fabOrange.setVisibility(View.GONE);
        fabGray.setVisibility(View.GONE);
    }

    private void openCreateNote(String color) {
        Intent intent = new Intent(this, NoteFormActivity.class);
        intent.putExtra(EXTRA_NOTE_COLOR, color);
        startActivity(intent);
    }

    private void loadNotes() {
        if (showingFavorites) {
            currentNotes = noteDao.getFavoriteNotes();
        } else {
            String query = etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                currentNotes = noteDao.searchNotesByTitle(query);
            } else {
                currentNotes = noteDao.getAllNotes();
            }
        }
        adapter.updateNotes(currentNotes);
        tvEmpty.setVisibility(currentNotes.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void filterNotes(String query) {
        if (showingFavorites) return;
        if (query.isEmpty()) {
            currentNotes = noteDao.getAllNotes();
        } else {
            currentNotes = noteDao.searchNotesByTitle(query);
        }
        adapter.updateNotes(currentNotes);
        tvEmpty.setVisibility(currentNotes.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onNoteClick(Note note) {
        Intent intent = new Intent(this, NoteFormActivity.class);
        intent.putExtra(EXTRA_NOTE_ID, note.getId());
        startActivity(intent);
    }

    @Override
    public void onNoteDoubleClick(Note note) {
        noteDao.toggleFavorite(note);
        loadNotes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }
}