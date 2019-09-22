package api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import model.Pokemon
import model.Sprite
import shared.ApplicationDispatcher
import shared.Image
import shared.toNativeImage

class PokemonApi {
    private val httpClient = HttpClient()


    fun getPokemon(pokemonId: Int, success: (Pokemon) -> Unit, failure: (Throwable?) -> Unit) {
        GlobalScope.launch(ApplicationDispatcher) {
            try {
                val url = "https://pokeapi.co/api/v2/pokemon/$pokemonId"
                val json = httpClient.get<String>(url)
                Json.nonstrict.parse(Pokemon.serializer(), json)
                    .also(success)
            } catch (ex: Exception) {
                failure(ex)
            }
        }
    }

    fun getPokemonSprite(sprite: Sprite, success: (Image?) -> Unit, failure: (Throwable?) -> Unit) {
        GlobalScope.launch(ApplicationDispatcher) {
            try {
                val url = sprite.frontDefault ?: (sprite.backDefault ?: "")
                httpClient.get<ByteArray>(url)
                    .toNativeImage()
                    .also(success)
            } catch (ex: Exception) {
                failure(ex)
            }
        }
    }
}