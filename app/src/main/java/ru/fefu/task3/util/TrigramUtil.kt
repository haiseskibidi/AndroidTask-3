package ru.fefu.task3.util

object TrigramUtil {

    private fun getTrigrams(text: String): Set<String> {
        val cleanText = text.lowercase().trim()
        if (cleanText.length < 3) return emptySet()
        
        return (0..cleanText.length - 3)
            .map { cleanText.substring(it, it + 3) }
            .toSet()
    }

    fun calculateSimilarity(query: String, target: String?): Float {
        if (target.isNullOrBlank()) return 0f
        if (query.isBlank()) return 0f
        
        if (target.contains(query, ignoreCase = true)) return 1.0f 

        val queryTrigrams = getTrigrams(query)
        val targetTrigrams = getTrigrams(target)

        if (queryTrigrams.isEmpty() || targetTrigrams.isEmpty()) return 0f

        val intersection = queryTrigrams.intersect(targetTrigrams).size
        val union = queryTrigrams.union(targetTrigrams).size

        return intersection.toFloat() / union.toFloat()
    }
}