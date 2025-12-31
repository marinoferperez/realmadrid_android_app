package com.example.real_madrid_museo.ui

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "BernabeuMuseo.db"
        private const val DATABASE_VERSION = 2

        // Nombres de columnas y tabla
        private const val TABLE_USERS = "users"
        private const val COL_ID = "id"
        private const val COL_EMAIL = "email"
        private const val COL_PASSWORD = "password"
        private const val COL_NAME = "name"
        private const val COL_PROFILE = "profile"
        
        private const val COL_VISITS = "visits"
        private const val COL_POINTS = "points"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE " + TABLE_USERS + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_EMAIL + " TEXT,"
                + COL_PASSWORD + " TEXT,"
                + COL_NAME + " TEXT,"
                + COL_PROFILE + " TEXT,"
                + COL_VISITS + " INTEGER DEFAULT 0,"
                + COL_POINTS + " INTEGER DEFAULT 0" + ")")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun addUser(email: String, pass: String, name: String, profile: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_EMAIL, email)
        values.put(COL_PASSWORD, pass)
        values.put(COL_NAME, name)
        values.put(COL_PROFILE, profile)
        values.put(COL_VISITS, 0)
        values.put(COL_POINTS, 0)

        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }

    fun checkUser(email: String, pass: String): Boolean {
        val db = this.readableDatabase
        val columns = arrayOf(COL_ID)
        val selection = "$COL_EMAIL = ? AND $COL_PASSWORD = ?"
        val selectionArgs = arrayOf(email, pass)

        val cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null)
        val count = cursor.count
        cursor.close()
        db.close()

        return count > 0
    }

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

    // AHORA CALCULA EL RANKING TAMBIÉN
    fun getUserDetails(email: String): Map<String, Any>? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", arrayOf(email))

        return if (cursor.moveToFirst()) {
            val points = cursor.getInt(cursor.getColumnIndexOrThrow(COL_POINTS))
            
            // CÁLCULO DE RANKING: Contar cuántos usuarios tienen estrictamente MÁS puntos
            val rankCursor = db.rawQuery("SELECT COUNT(*) FROM users WHERE $COL_POINTS > ?", arrayOf(points.toString()))
            var rank = 1
            if (rankCursor.moveToFirst()) {
                rank = rankCursor.getInt(0) + 1 // Si hay 2 personas con más puntos, yo soy el 3º
            }
            rankCursor.close()

            val userData = mapOf(
                "name" to cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                "profile" to cursor.getString(cursor.getColumnIndexOrThrow(COL_PROFILE)),
                "visits" to cursor.getInt(cursor.getColumnIndexOrThrow(COL_VISITS)),
                "points" to points,
                "ranking" to rank // Nuevo dato
            )
            cursor.close()
            db.close()
            userData
        } else {
            cursor.close()
            db.close()
            null
        }
    }

    fun incrementVisits(email: String) {
        val db = this.writableDatabase
        db.execSQL("UPDATE $TABLE_USERS SET $COL_VISITS = $COL_VISITS + 1 WHERE $COL_EMAIL = ?", arrayOf(email))
        db.close()
    }

    fun addPoints(email: String, pointsToAdd: Int) {
        val db = this.writableDatabase
        db.execSQL("UPDATE $TABLE_USERS SET $COL_POINTS = $COL_POINTS + ? WHERE $COL_EMAIL = ?", arrayOf(pointsToAdd, email))
        db.close()
    }

    // Obtener lista completa para el Ranking
    fun getAllUsersRanking(): List<Pair<String, Int>> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COL_NAME, $COL_POINTS FROM $TABLE_USERS ORDER BY $COL_POINTS DESC", null)
        val list = mutableListOf<Pair<String, Int>>()
        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(0)
                val points = cursor.getInt(1)
                list.add(name to points)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }
}
