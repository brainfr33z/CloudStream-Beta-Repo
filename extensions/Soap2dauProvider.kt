package soap2dau

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element

class Soap2dauProvider : MainAPI() {
    override var mainUrl = "https://soap2dau.xyz"
    override var name = "Soap2dau"
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)
    
    override suspend fun search(query: String): List<SearchResponse> {
        val url = "$mainUrl/search.html?keyword=$query"
        val document = app.get(url).document
        return document.select(".video-block a").map {
            val title = it.select("h3").text()
            val href = it.attr("href")
            val poster = it.select("img").attr("src")
            newMovieSearchResponse(title, "$mainUrl$href") {
                this.posterUrl = poster
            }
        }
    }
    
    override suspend fun load(url: String): LoadResponse {
        val document = app.get(url).document
        val title = document.select("h1").text()
        val poster = document.select(".poster img").attr("src")
        val streamUrl = document.select("source").attr("src")
        return newMovieLoadResponse(title, url, TvType.Movie) {
            this.posterUrl = poster
            addSource(streamUrl, "Default")
        }
    }
}
