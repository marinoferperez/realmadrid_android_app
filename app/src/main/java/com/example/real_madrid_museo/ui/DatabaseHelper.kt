package com.example.real_madrid_museo.ui

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "BernabeuMuseo.db"
        private const val DATABASE_VERSION = 1

        // Nombres de columnas y tabla
        private const val TABLE_USERS = "users"
        private const val COL_ID = "id"
        private const val COL_EMAIL = "email"
        private const val COL_PASSWORD = "password"
        private const val COL_NAME = "name"
        private const val COL_PROFILE = "profile" // NIÑO o ADULTO
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Crear la tabla de usuarios
        val createTable = ("CREATE TABLE " + TABLE_USERS + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_EMAIL + " TEXT,"
                + COL_PASSWORD + " TEXT,"
                + COL_NAME + " TEXT,"
                + COL_PROFILE + " TEXT" + ")")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // Función para registrar un usuario
    fun addUser(email: String, pass: String, name: String, profile: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_EMAIL, email)
        values.put(COL_PASSWORD, pass)
        values.put(COL_NAME, name)
        values.put(COL_PROFILE, profile)

        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result != -1L // Devuelve true si se guardó bien
    }

    // Función para comprobar Login
    fun checkUser(email: String, pass: String): Boolean {
        val db = this.readableDatabase
        val columns = arrayOf(COL_ID)
        val selection = "$COL_EMAIL = ? AND $COL_PASSWORD = ?"
        val selectionArgs = arrayOf(email, pass)

        val cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null)
        val count = cursor.count
        cursor.close()
        db.close()

        return count > 0 // Devuelve true si encontró al usuario
    }

    // Función para comprobar si el email ya existe (para no repetirlos)
    fun checkEmailExists(email: String): Boolean {
        val db = this.readableDatabase
        val columns = arrayOf(COL_ID)
        val selection = "$COL_EMAIL = ?"
        val selectionArgs = arrayOf(email)
        val cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null)
        val count = cursor.count
        cursor.close()
        return count > 0
    }

    fun getUserDetails(email: String): Map<String, String>? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", arrayOf(email))

        return if (cursor.moveToFirst()) {
            val userData = mapOf(
                "name" to cursor.getString(cursor.getColumnIndexOrThrow("name")),
                "profile" to cursor.getString(cursor.getColumnIndexOrThrow("profile"))
            )
            cursor.close()
            userData
        } else {
            cursor.close()
            null
        }
    }
}

// Función para obtener los datos completos de un usuario por su email
