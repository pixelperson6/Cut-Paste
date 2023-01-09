package com.codingwithpix3l.photoonphoto.ui.main.nav

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.codingwithpix3l.photoonphoto.ui.theme.PhotoOnPhotoTheme
import com.codingwithpix3l.photoonphoto.R
import com.codingwithpix3l.photoonphoto.core.util.openEmailComposer
import com.codingwithpix3l.photoonphoto.core.util.openUrl

class AboutActivity : ComponentActivity() {

    companion object {
        const val DEVELOPER_EMAIL = "pixelperson6@gmail.com"
        const val PRIVACY_POLICY_URL =
            "https://pix3lpoint.blogspot.com/2022/01/cut-paste-photo-on-photo-privacy-policy.html"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhotoOnPhotoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    AboutScreen(
                        onSupportClicked = {
                            startActivity(Intent(this@AboutActivity, SupportActivity::class.java))
                            finish()
                        },
                        onBackPressed = {
                            onBackPressedDispatcher.addCallback(this /* lifecycle owner */, object : OnBackPressedCallback(true) {
                                override fun handleOnBackPressed() {
                                    // Back is pressed... Finishing the activity
                                    finish()
                                }
                            })
                           // onBackPressed()
                        },
                        onDeveloperClicked = {
                            openEmailComposer( DEVELOPER_EMAIL)
                        },
                        onPolicyClicked = {
                            openUrl(PRIVACY_POLICY_URL)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AboutScreen(
    onSupportClicked:()->Unit,
    onBackPressed:()->Unit,
    onDeveloperClicked:()->Unit,
    onPolicyClicked:()->Unit
) {

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {

        val (
            box, title, slogan, logo, backBtn, version, developer, policy, about, supportBtn
        ) = createRefs()

        val guildLineFromTop = createGuidelineFromTop(0.09f)

        Box(
            modifier = Modifier
                .fillMaxHeight(0.26f)
                .clip(RoundedCornerShape(bottomEnd = 20.dp, bottomStart = 20.dp))
                .background(MaterialTheme.colors.primary)
                .constrainAs(box) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    width = Dimension.fillToConstraints
                }
        )
        IconButton(
            onClick = {

                      onBackPressed()
            },
            modifier = Modifier
                .constrainAs(backBtn) {
                    start.linkTo(parent.start, 16.dp)
                    top.linkTo(parent.top, 16.dp)

                }
        ) {
            Icon(modifier = Modifier.size(35.dp),
                imageVector = Icons.Default.ReplyAll,
                contentDescription = "back button"
            )
        }

        Icon(
            modifier = Modifier
                .size(80.dp)
                .constrainAs(logo) {
                    start.linkTo(parent.start, 40.dp)
                    top.linkTo(guildLineFromTop)
                },
            painter = painterResource(id = R.drawable.ic_launcher_foreground2),
            tint = Color.Unspecified,
            contentDescription = "Logo"
        )

        Text(
            modifier = Modifier
                .constrainAs(title) {
                    start.linkTo(logo.end,8.dp)
                    top.linkTo(guildLineFromTop)
                },
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.h4
        )

        Text(
            modifier = Modifier
                .constrainAs(slogan) {
                    start.linkTo(logo.end, 8.dp)
                    top.linkTo(title.bottom)
                },
            text = stringResource(R.string.slogan),
            style = MaterialTheme.typography.body1
        )

        Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colors.secondary)
            .constrainAs(version) {
                top.linkTo(box.bottom, 8.dp)
                start.linkTo(parent.start, 8.dp)
                end.linkTo(parent.end, 8.dp)
                width = Dimension.fillToConstraints
            }
            .padding(16.dp)

        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.Default.Code, contentDescription = "version")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.version_txt), style = MaterialTheme.typography.body1)
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colors.secondary)
                .constrainAs(developer) {
                    top.linkTo(version.bottom, 8.dp)
                    start.linkTo(parent.start, 8.dp)
                    end.linkTo(parent.end, 8.dp)
                    width = Dimension.fillToConstraints
                }
                .clickable {
                    onDeveloperClicked()

                }
                .padding(16.dp)

        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.Default.Copyright, contentDescription = "developer")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Contact Developer", style = MaterialTheme.typography.body1)
        }
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colors.secondary)
                .constrainAs(policy) {
                    top.linkTo(developer.bottom, 8.dp)
                    start.linkTo(parent.start, 8.dp)
                    end.linkTo(parent.end, 8.dp)
                    width = Dimension.fillToConstraints
                }
                .clickable {
                    onPolicyClicked()

                }
                .padding(16.dp)

        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.Default.Security, contentDescription = "privacy")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Privacy policy", style = MaterialTheme.typography.body1)
        }


        Text(
            modifier = Modifier
                .constrainAs(about) {
                    start.linkTo(parent.start, 16.dp)
                    end.linkTo(parent.end, 16.dp)
                    top.linkTo(policy.bottom, 16.dp)
                }
                .padding(8.dp),
            text = stringResource(id = R.string.add_photo_on_photo_brief_description),

            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onPrimary
            )

        Row(
            modifier = Modifier
                .wrapContentSize()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colors.secondary)
                .constrainAs(supportBtn) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(about.bottom, 8.dp)
                    bottom.linkTo(parent.bottom)
                }
                .clickable {

                    onSupportClicked()

                }
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.Default.Favorite, contentDescription = "policy")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Support app")
        }

    }
}


@Preview
@Composable
fun About() {
    AboutScreen({},{},{},{})
}

