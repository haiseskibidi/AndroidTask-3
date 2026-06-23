package ru.fefu.task3.data.network.dto

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * тесты для DtoMapper.
 * проверяем логику очистки текста и трансформации DTO в домен.
 */
class DtoMapperTest {

    @Test
    fun `toDomain details should clean description from BB-codes and stray brackets`() {
        val dto = AnimeDetailsDto(
            id = 1,
            name = "Test",
            russian = "Тест",
            image = null,
            score = null,
            kind = null,
            status = null,
            description = "[character=123]Character[/character] is ] bored [anime=456]Title[/anime]",
            descriptionHtml = null,
            episodes = null,
            airedOn = null,
            genres = null
        )

        val domain = dto.toDomain()

        // проверяем, что маппер очистил текст
        assertThat(domain.description).isEqualTo("Character is bored Title")
    }

    @Test
    fun `toDomain details should prefer html description if plain is missing`() {
        val dto = AnimeDetailsDto(
            id = 1,
            name = "Test",
            russian = null,
            image = null,
            score = null,
            kind = null,
            status = null,
            description = null,
            descriptionHtml = "<b>HTML</b> [tag]Text[/tag]",
            episodes = null,
            airedOn = null,
            genres = null
        )

        val domain = dto.toDomain()

        // проверяем очистку и HTML, и тегов
        assertThat(domain.description).isEqualTo("HTML Text")
    }
}
