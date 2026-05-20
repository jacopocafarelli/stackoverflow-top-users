package com.jc.topstackoverflowusers.data.local

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class UsersFollowLocalDataSourceTest {

    @get:Rule
    val tempFolder = TemporaryFolder()
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val dataStore = PreferenceDataStoreFactory.create(
        scope = testScope,
        produceFile = { tempFolder.newFile("followed_users.preferences_pb") }
    )
    private val dataSource = UsersFollowLocalDataSource(dataStore)

    @Test
    fun `when no follow yet data source emits empty set`() = runTest {
        val followedUsers = dataSource.observeFollowedUsers().first()

        assertThat(followedUsers).isEmpty()
    }

    @Test
    fun `when followed users data source emits the ids`() = runTest {
        dataSource.followUser(1)
        dataSource.followUser(2)

        val followedUsers = dataSource.observeFollowedUsers().first()
        assertThat(followedUsers).isEqualTo(setOf(1, 2))
    }

    @Test
    fun `when following the same user multiple times data source emits it only once`() = runTest {
        dataSource.followUser(1)
        dataSource.followUser(1)

        val followedUsers = dataSource.observeFollowedUsers().first()
        assertThat(followedUsers).isEqualTo(setOf(1))
    }

    @Test
    fun `when unfollowing an user data source removes the user`() = runTest {
        dataSource.followUser(1)
        dataSource.unfollowUser(1)

        val followedUsers = dataSource.observeFollowedUsers().first()
        assertThat(followedUsers).isEqualTo(emptySet<Int>())
    }

    @Test
    fun `when unfollowing an user which was not followed should not produce errors`() = runTest {
        dataSource.unfollowUser(1)

        val followedUsers = dataSource.observeFollowedUsers().first()
        assertThat(followedUsers).isEqualTo(emptySet<Int>())
    }
}