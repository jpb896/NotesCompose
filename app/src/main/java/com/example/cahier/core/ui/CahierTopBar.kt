/*
 * Copyright 2025 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.cahier.core.ui

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.cahier.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CahierTopBar(
    title: String,
    onSearchClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    isSearchVisible: Boolean = false,
    isProfileVisible: Boolean = false,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            if (isSearchVisible) {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.search_24px),
                        contentDescription = "Search"
                    )
                }
            }
            if (isProfileVisible) {
                IconButton(onClick = onProfileClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.account_circle_24px),
                        contentDescription = "Profile"
                    )
                }
            }
        },
        modifier = modifier
    )
}