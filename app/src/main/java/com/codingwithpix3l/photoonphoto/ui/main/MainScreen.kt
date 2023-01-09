package com.codingwithpix3l.photoonphoto.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.codingwithpix3l.photoonphoto.R
import com.codingwithpix3l.photoonphoto.core.components.AdvertView
import com.google.android.gms.ads.AdSize

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onSupportClicked: () -> Unit,
    onTipsClicked: () -> Unit,
    onAboutClicked: () -> Unit,
    onGalleryCLicked: () -> Unit,
    onCollageCLicked: () -> Unit,
    onExtraClicked: () -> Unit,
    onSavedClicked: () -> Unit,
) {

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
    ) {

        val (
            box, dashBoard, option, gallery, collage, extra,saved,adView
        ) = createRefs()

        val guildLineFromTop = createGuidelineFromTop(0.2f)



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

        Text(
            modifier = Modifier
                .wrapContentSize()
                .constrainAs(dashBoard) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top, 32.dp)
                    end.linkTo(option.start, 16.dp)
                    horizontalChainWeight = 0f
                },
            text = "Dashboard",
            style = MaterialTheme.typography.h4,
            color = MaterialTheme.colors.onPrimary
        )
        DashboardOption(
            modifier = Modifier
                .constrainAs(option) {
                    top.linkTo(parent.top, 30.dp)
                    end.linkTo(parent.end)
                    start.linkTo(dashBoard.end,16.dp)
                    horizontalChainWeight = 0f
                },
            onAboutClicked = onAboutClicked,
            onSupportClicked = onSupportClicked,
            onTipsClicked = onTipsClicked
        )



        FeatureBox(
            modifier = Modifier
                .constrainAs(gallery) {
                    top.linkTo(guildLineFromTop,8.dp)
                    start.linkTo(parent.start,16.dp)
                    end.linkTo(collage.start,16.dp)


                },
            onClick = { onGalleryCLicked() },
            label = "Background \nChanger",
            description ="background changer",
            iconPainter = painterResource(id = R.drawable.gallery)
        )
        //feature saved

        FeatureBox(
            modifier = Modifier
                .constrainAs(saved) {
                    top.linkTo(collage.bottom,24.dp)
                    start.linkTo(extra.end)
                    end.linkTo(parent.end)
                },
            onClick = { onSavedClicked() },
            label = "Saved\nImage",
            description ="photos on photo",
            iconPainter = painterResource(id = R.drawable.collection)
        )


        //feature collage
        FeatureBox(
            modifier = Modifier
                .constrainAs(collage) {
                    top.linkTo(guildLineFromTop,8.dp)
                    start.linkTo(gallery.end)
                    end.linkTo(parent.end,16.dp)
                },
            onClick = { onCollageCLicked() },
            label = "Photo \nCollage",
            description ="photo collage",
            iconPainter = painterResource(id = R.drawable.collage)
        )


        //feature collection

        FeatureBox(
            modifier = Modifier
                .constrainAs(extra) {
                    top.linkTo(gallery.bottom,24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(saved.start)

                },
            onClick = { onExtraClicked() },
            label = "Free style\nCollage",
            description ="photos on photo",
            iconPainter = painterResource(id = R.drawable.free_collage)
        )
        

        AdvertView(modifier = Modifier
            .constrainAs(adView){
                top.linkTo(extra.bottom,16.dp)
                start.linkTo(parent.start,8.dp)
                end.linkTo(parent.end,8.dp)
                bottom.linkTo(parent.bottom,8.dp)
                width = Dimension.fillToConstraints
                height= Dimension.fillToConstraints

        },
            adId = stringResource(id = R.string.ad_unit_main),
            adSize = AdSize.LARGE_BANNER
        )
    }
}

@Composable
fun FeatureBox(
    modifier: Modifier=Modifier,
    onClick :() ->Unit,
    label:String,
    description:String,
    iconPainter:Painter

) {
    Column(
        modifier = modifier
            .width(130.dp)
            .height(140.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                onClick()
            }
            .background(MaterialTheme.colors.secondary)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier
                .width(45.dp)
                .height(45.dp),
            painter = iconPainter,
            tint = Color.Unspecified,
            contentDescription = description
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSecondary,
            textAlign = TextAlign.Center
        )

    }

}

@Composable
fun DashboardOption(
    modifier: Modifier = Modifier,
    onSupportClicked: () -> Unit,
    onTipsClicked: () -> Unit,
    onAboutClicked: () -> Unit
) {

    Row(
        modifier = modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(30.dp))
            .background(MaterialTheme.colors.surface),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = {
                onTipsClicked()
            },
        ) {
            Icon(
                modifier = Modifier.size(30.dp),
                imageVector = Icons.Default.Help,
                tint = MaterialTheme.colors.onPrimary,
                contentDescription = "help"
            )
        }
        IconButton(
            onClick = {
                onSupportClicked()

            },
        ) {
            Icon(
                modifier = Modifier.size(35.dp),
                painter = painterResource(id = R.drawable.ic_round_favorite_24),
                tint = MaterialTheme.colors.onPrimary,
                contentDescription = "support"
            )
        }

        IconButton(
            onClick = {
                onAboutClicked()
            },
        ) {
            Icon(
                modifier = Modifier.size(35.dp),
                imageVector = Icons.Default.Info,
                tint = MaterialTheme.colors.onPrimary,
                contentDescription = "about"
            )
        }

    }
}

@Preview
@Composable
fun FeatureBoxPreview() {
    MainScreen(
        onSupportClicked = {},
        onTipsClicked = { },
        onAboutClicked = {},
        onGalleryCLicked = {},
        onCollageCLicked = {},
        onSavedClicked = {},
        onExtraClicked = {}
    )
}



