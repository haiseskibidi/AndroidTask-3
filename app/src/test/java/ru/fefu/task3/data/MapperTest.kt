package ru.fefu.task3.data

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import ru.fefu.task3.data.db.AnimeEntity
import ru.fefu.task3.domain.model.AnimeDetails

/**
 * юнит-тесты для мапперов.
 * проверяем корректность преобразования моделей между слоями.
 */
class MapperTest {

    @Test
    fun `entity to domain mapping should be correct`() {
        val entity = AnimeEntity(
            id = 1,
            name = "Test",
            russian = "Тест",
            imageUrl = "https://example.com/img.jpg",
            score = "8.5",
            kind = "tv",
            status = "released"
        )

        val domain = entity.toAnimeBase()

        assertThat(domain.id).isEqualTo(entity.id)
        assertThat(domain.name).isEqualTo(entity.name)
        assertThat(domain.imageUrl).isEqualTo(entity.imageUrl)
        assertThat(domain.score).isEqualTo(entity.score)
    }

    @Test
    fun `domain details to entity mapping should be correct`() {
        val domain = AnimeDetails(
            id = 1,
            name = "Test",
            russian = "Тест",
            imageUrl = "https://example.com/img.jpg",
            score = "8.5",
            kind = "tv",
            status = "released",
            description = "Clean desc",
            descriptionHtml = null,
            episodes = 12,
            airedOn = "2023",
            genres = emptyList()
        )

        val entity = domain.toEntity()

        assertThat(entity.id).isEqualTo(domain.id)
        assertThat(entity.name).isEqualTo(domain.name)
        assertThat(entity.imageUrl).isEqualTo(domain.imageUrl)
    }
}
