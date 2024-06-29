package id.project.stories.data.local

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "story")
@Parcelize
data class StoryModel(
    @PrimaryKey
    val id: String,

    val name: String,
    val photoUrl: String,
    val description: String,
    val createdAt: String,
    val lan: Double? = null,
    val lon: Double? = null
) : Parcelable