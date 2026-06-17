package com.example.notesapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.notesapp.model.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteDao {

    private final DatabaseHelper dbHelper;

    public NoteDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insertNote(Note note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, note.getTitle());
        values.put(DatabaseHelper.COLUMN_CONTENT, note.getContent());
        values.put(DatabaseHelper.COLUMN_COLOR, note.getColor());
        values.put(DatabaseHelper.COLUMN_IS_FAVORITE, note.isFavorite() ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_DATE, note.getDate());
        long id = db.insert(DatabaseHelper.TABLE_NOTES, null, values);
        db.close();
        return id;
    }

    public int updateNote(Note note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, note.getTitle());
        values.put(DatabaseHelper.COLUMN_CONTENT, note.getContent());
        values.put(DatabaseHelper.COLUMN_COLOR, note.getColor());
        values.put(DatabaseHelper.COLUMN_IS_FAVORITE, note.isFavorite() ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_DATE, note.getDate());
        int rows = db.update(DatabaseHelper.TABLE_NOTES, values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
        return rows;
    }

    public int deleteNote(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(DatabaseHelper.TABLE_NOTES,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public List<Note> getAllNotes() {
        return queryNotes(null, null, null);
    }

    public List<Note> getFavoriteNotes() {
        return queryNotes(
                DatabaseHelper.COLUMN_IS_FAVORITE + " = ?",
                new String[]{"1"}, null);
    }

    public List<Note> searchNotesByTitle(String query) {
        return queryNotes(
                DatabaseHelper.COLUMN_TITLE + " LIKE ?",
                new String[]{"%" + query + "%"}, null);
    }

    public int toggleFavorite(Note note) {
        note.setFavorite(!note.isFavorite());
        return updateNote(note);
    }

    private List<Note> queryNotes(String selection, String[] selectionArgs, String orderBy) {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NOTES, null,
                selection, selectionArgs,
                null, null,
                orderBy != null ? orderBy : DatabaseHelper.COLUMN_ID + " DESC"
        );
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONTENT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COLOR)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_FAVORITE)) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE))
                );
                notes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notes;
    }
}