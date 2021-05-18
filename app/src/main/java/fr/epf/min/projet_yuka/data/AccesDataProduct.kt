
package fr.epf.min.projet_yuka.data


import retrofit2.http.GET
import retrofit2.http.Path


interface AccesDataProduct {
    @GET("/api/v0/product/{id}")
    suspend fun getProducts(@Path("id") idproduct : Long?): GetProductsResult
}

data class GetProductsResult(val product: Products)

data class Products(val product_name: String,val nutriscore_grade:String,val ingredients_text:String,val image_front_small_url:String,val image_url:String,val code:Long)
