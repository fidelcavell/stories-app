package id.project.stories.utils

import id.project.stories.data.remote.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "photoUrl + $i",
                "CreateAt + $i",
                "name + $i",
                "desc + $i",
                0.0 + i,
                "id + $i",
                0.0 + i
            )
            items.add(story)
        }
        return items
    }
}