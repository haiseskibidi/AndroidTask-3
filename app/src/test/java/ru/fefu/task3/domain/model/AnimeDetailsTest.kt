package ru.fefu.task3.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * простые тесты для доменной модели.
 */
class AnimeDetailsTest {

    @Test
    fun `displayName should prefer russian name`() {
        val details = AnimeDetails(
            id = 1,
            name = "Test",
            russian = "Тест",
            imageUrl = null,
            score = null,
            kind = null,
            status = null,
            description = null,
            descriptionHtml = null,
            episodes = null,
            airedOn = null,
            genres = null
        )

        assertThat(details.getDisplayName()).isEqualTo("Тест")
    }

    @Test
    fun `displayName should fallback to english name if russian is missing`() {
        val details = AnimeDetails(
            id = 1,
            name = "Test",
            russian = null,
            imageUrl = null,
            score = null,
            kind = null,
            status = null,
            description = null,
            descriptionHtml = null,
            episodes = null,
            airedOn = null,
            genres = null
        )

        assertThat(details.getDisplayName()).isEqualTo("Test")
    }
}

