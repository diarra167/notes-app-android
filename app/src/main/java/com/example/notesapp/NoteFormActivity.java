package com.example.notesapp;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notesapp.database.NoteDao;
import com.example.notesapp.model.Note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteFormActivity extends AppCompatActivity {

    private EditText etTitle;
    private EditText etContent;
    private Button btnSave;
    private NoteDao noteDao;
    private Note existingNote = null;
    private String selectedColor = "219653";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_form);

        noteDao = new NoteDao(this);
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        btnSave = findViewById(R.id.btn_save);

        int noteId = getIntent().getIntExtra(MainActivity.EXTRA_NOTE_ID, -1);
        if (noteId != -1) {
            loadExistingNote(noteId);
        } else {
            selectedColor = getIntent().getStringExtra(MainActivity.EXTRA_NOTE_COLOR);
            if (selectedColor == null) selectedColor = "219653";
            applyColor(selectedColor);
            btnSave.setText("Créer");
        }

        btnSave.setOnClickListener(v -> saveNote());
    }

    private void loadExistingNote(int id) {
        List<Note> all = noteDao.getAllNotes();
        for (Note n : all) {
            if (n.getId() == id) { existingNote = n; break; }
        }
        if (existingNote != null) {
            etTitle.setText(existingNote.getTitle());
            etContent.setText(existingNote.getContent());
            selectedColor = existingNote.getColor();
            applyColor(selectedColor);
            btnSave.setText("Modifier");
        }
    }

    private void applyColor(String hexColor) {
        try {
            int color = Color.parseColor("#" + hexColor);
            etTitle.setBackgroundColor(color);
            etContent.setBackgroundColor(color);
        } catch (Exception e) {
            // ignore invalid color
        }
    }

    private void saveNote() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Le titre et le contenu sont obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = new SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH).format(new Date());

        if (existingNote != null) {
            existingNote.setTitle(title);
            existingNote.setContent(content);
            existingNote.setColor(selectedColor);
            existingNote.setDate(date);
            noteDao.updateNote(existingNote);
            Toast.makeText(this, "Note modifiée", Toast.LENGTH_SHORT).show();
        } else {
            Note newNote = new Note(0, title, content, selectedColor, false, date);
            noteDao.insertNote(newNote);
            Toast.makeText(this, "Note créée", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}

