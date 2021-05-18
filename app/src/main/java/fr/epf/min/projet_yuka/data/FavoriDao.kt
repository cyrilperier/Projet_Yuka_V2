package fr.epf.min.projet_yuka.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import fr.epf.min.projet_yuka.model.Favori

@Dao
interface FavoriDao {

    @Query("select * from favoris")
    suspend fun getAllFavoris(): List<Favori>

    @Query("SELECT * FROM favoris WHERE code LIKE :code  LIMIT 1")
    suspend fun findByCode(code: Long): Favori

    @Insert
    suspend fun addFavori(favori: Favori)

    @Delete
    suspend fun deleteFavori(favori:Favori)
}