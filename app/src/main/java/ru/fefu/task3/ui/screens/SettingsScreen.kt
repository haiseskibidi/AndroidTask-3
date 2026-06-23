package ru.fefu.task3.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.fefu.task3.domain.model.User
import androidx.compose.ui.tooling.preview.Preview
import ru.fefu.task3.ui.theme.Task3Theme
import androidx.compose.ui.res.stringResource
import ru.fefu.task3.R
import ru.fefu.task3.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentTheme: Boolean?,
    allUsers: List<User>,
    activeUserId: Long?,
    onThemeChange: (Boolean) -> Unit,
    onUserSelect: (Long) -> Unit,
    onUserCreate: (String) -> Unit,
    onUserDelete: (Long) -> Unit,
    onClearHistory: () -> Unit,
    onBackClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var newUserName by remember { mutableStateOf("") }
    var userToDelete by remember { mutableStateOf<User?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().padding(MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            item {
                Text(stringResource(R.string.settings_design_label), style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = MaterialTheme.spacing.small),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.settings_dark_theme))
                    Switch(
                        checked = currentTheme == true,
                        onCheckedChange = onThemeChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
                HorizontalDivider()
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.settings_users_title), style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            }

            items(allUsers, key = { it.id }) { user ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onUserSelect(user.id) }
                        .padding(vertical = MaterialTheme.spacing.small),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(user.name, style = MaterialTheme.typography.bodyLarge)
                    if (user.id == activeUserId) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    } else {
                        IconButton(onClick = { userToDelete = user }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.settings_delete_profile),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            item {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                Button(
                    onClick = onClearHistory,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.settings_clear_history), color = MaterialTheme.colorScheme.onError)
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.settings_new_profile_title)) },
            text = { OutlinedTextField(value = newUserName, onValueChange = { newUserName = it }, label = { Text(stringResource(R.string.settings_username_label)) }, singleLine = true) },
            confirmButton = { TextButton(onClick = { if (newUserName.isNotBlank()) { onUserCreate(newUserName); newUserName = ""; showDialog = false } }) { Text(stringResource(R.string.settings_create_button)) } },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text(stringResource(R.string.settings_cancel_button)) } }
        )
    }

    userToDelete?.let { user ->
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = { Text(stringResource(R.string.settings_delete_profile_confirm_title)) },
            text = { Text(stringResource(R.string.settings_delete_profile_confirm_desc, user.name)) },
            confirmButton = { TextButton(onClick = { onUserDelete(user.id); userToDelete = null }) { Text(stringResource(R.string.settings_delete_confirm_action), color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { userToDelete = null }) { Text(stringResource(R.string.settings_cancel_button)) } }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    Task3Theme {
        SettingsScreen(
            currentTheme = false,
            allUsers = listOf(
                User(id = 1L, name = "Главный профиль"),
                User(id = 2L, name = "Другой пользователь")
            ),
            activeUserId = 1L,
            onThemeChange = {},
            onUserSelect = {},
            onUserCreate = {},
            onUserDelete = {},
            onClearHistory = {},
            onBackClick = {}
        )
    }
}
