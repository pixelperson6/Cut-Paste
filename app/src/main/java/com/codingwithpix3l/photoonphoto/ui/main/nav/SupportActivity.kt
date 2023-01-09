package com.codingwithpix3l.photoonphoto.ui.main.nav

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReplyAll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.codingwithpix3l.photoonphoto.R
import com.codingwithpix3l.photoonphoto.core.util.openEmailComposer
import com.codingwithpix3l.photoonphoto.core.util.openShareWithChooser
import com.codingwithpix3l.photoonphoto.core.util.openUrl
import com.codingwithpix3l.photoonphoto.ui.theme.PhotoOnPhotoTheme

class SupportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhotoOnPhotoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    SupportScreen(
                        onBackPressed = {
                          //  onBackPressed()
                            onBackPressedDispatcher.addCallback(this /* lifecycle owner */, object : OnBackPressedCallback(true) {
                                override fun handleOnBackPressed() {
                                    // Back is pressed... Finishing the activity
                                    finish()
                                }
                            })
                        },
                        onRatePressed = {
                            openUrl(GOOGLE_PLAY_URL)
                        },
                        onSharePressed = {
                            val shareText =
                                "${getString(R.string.text_share_with_friends)}\n\n$GOOGLE_PLAY_URL"
                            openShareWithChooser(shareText)
                        },
                        onDeveloperPressed = {
                            openEmailComposer(AboutActivity.DEVELOPER_EMAIL)
                        }
                    )

                }
            }
        }
    }


    companion object {
        const val GOOGLE_PLAY_URL =
            "https://play.google.com/store/apps/details?id=com.codingwithpix3l.photoonphoto"


        /*const val FACEBOOK_URL = "https://www.facebook.com/Forgetmenot-Flashcards-103107271588768"
        const val INSTAGRAM_URL = "https://www.instagram.com/forgetmenot_flashcards"
        const val TWITTER_URL = "https://twitter.com/ForgetMeNot_FC"
        const val YOUTUBE_URL = "https://www.youtube.com/channel/UC5Hst5gp1HPLqCeGgKbAI9g"*/
    }

}

@Composable
fun SupportScreen(
    onBackPressed: () -> Unit,
    onRatePressed: () -> Unit,
    onSharePressed: () -> Unit,
    onDeveloperPressed: () -> Unit,

    ) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally


    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight(0.1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomEnd = 20.dp, bottomStart = 20.dp))
                .background(MaterialTheme.colors.primary),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(
                onClick = {
                    onBackPressed()
                }
            ) {
                Icon(
                    modifier = Modifier.size(35.dp),
                    imageVector = Icons.Default.ReplyAll,
                    contentDescription = "back button"
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            Text(text = "Support App", style = MaterialTheme.typography.h5)
        }
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            item {


                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colors.secondary)
                        .padding(8.dp)
                ) {

                    Text(
                        text = stringResource(R.string.rate_us_header),
                        style = MaterialTheme.typography.h6
                    )
                    Text(
                        text = stringResource(id = R.string.rate_us_body),
                        style = MaterialTheme.typography.body1
                    )

                    Button(onClick = {
                        onRatePressed()
                    }, modifier = Modifier.align(Alignment.End)) {

                        Text(text = stringResource(id = R.string.rate_us))
                    }

                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colors.secondary)
                        .padding(8.dp)
                ) {

                    Text(
                        text = stringResource(id = R.string.feedback_head),
                        style = MaterialTheme.typography.h6
                    )
                    Text(
                        text = stringResource(id = R.string.feedback_body),
                        style = MaterialTheme.typography.body1
                    )

                    Button(onClick = {
                        onRatePressed()
                    }, modifier = Modifier.align(Alignment.End)) {

                        Text(text = stringResource(id = R.string.write_review_on_google_play))
                    }
                    Button(
                        onClick = { onDeveloperPressed() },
                        modifier = Modifier.align(Alignment.End)
                    ) {

                        Text(text = stringResource(id = R.string.send_email_to_developer))
                    }

                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colors.secondary)
                        .padding(8.dp)
                ) {

                    Text(
                        text = stringResource(id = R.string.share_with_friends),
                        style = MaterialTheme.typography.h6
                    )
                    Text(
                        text = stringResource(id = R.string.share_with_friends_body),
                        style = MaterialTheme.typography.body1
                    )

                    Button(
                        onClick = { onSharePressed() },
                        modifier = Modifier.align(Alignment.End)
                    ) {

                        Text(text = "Share with friends")
                    }

                }

            }
        }
    }

}
