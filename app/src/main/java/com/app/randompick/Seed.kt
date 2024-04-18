package com.app.randompick

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "table_seed")
data class Seed(
    @PrimaryKey
    var seedId: Long,
    var groupId: Long = -1L,
    var value: String,
    /**
     * Only long supported
     */
    val isNumber: Boolean = false
) {
    fun toNumber() = value.toLong()
}

/**
 * Seed group
 *
 * 1 -> N
 *
 * @property groupId
 * @property count
 * @property title
 * @constructor Create empty Seed group
 */
@Entity(tableName = "table_seed_group")
data class SeedGroup(
    @PrimaryKey
    val groupId: Long,
    var title: String,
    var usedFor2Nums: Boolean= false
)

data class SeedGroups(
    @Embedded val group: SeedGroup,
    @Relation(
        parentColumn = "groupId",
        entityColumn = "groupId"
    )
    val seeds: List<Seed>
)
