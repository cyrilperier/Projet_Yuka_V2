package fr.epf.min.projet_yuka.data

import androidx.room.Database
import androidx.room.RoomDatabase
import fr.epf.min.projet_yuka.model.Favori

@Database(entities = [Favori::class], version = 1)
abstract class FavoriDataBase: RoomDatabase() {

    abstract fun getFavoriDao() : FavoriDao

}

