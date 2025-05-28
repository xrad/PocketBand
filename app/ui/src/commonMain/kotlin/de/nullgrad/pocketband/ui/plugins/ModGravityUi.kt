package de.nullgrad.pocketband.ui.plugins

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.nullgrad.pocketband.plugins.modulators.GravityLiveState
import de.nullgrad.pocketband.plugins.modulators.ModGravity
import de.nullgrad.pocketband.ui.screens.edit.EditPluginViewmodel
import de.nullgrad.pocketband.ui.theme.Theme
import de.nullgrad.pocketband.ui.widgets.ParameterColumn
import kotlinx.coroutines.flow.filterIsInstance

object ModGravityUi : PluginUi {
    override val type = ModGravity.PLUGIN_TYPE

    override val parameterLabel = emptyMap<String,String>()

    @Composable
    override fun createController() {
        ModGravityUi()
    }
}

private val colorBackground = Color.Gray
private val colorX = Color.Red
private val colorY = Color.Green
private val colorZ = Color.Blue
private val colorCenter = Color.White

@Composable
private fun ModGravityUi(modifier: Modifier = Modifier) {
    ParameterColumn(modifier = modifier) {
        Visualizer()
        ParameterColumn(modifier = Modifier.padding(Theme.spacing.normal)) {
            IndicatingModulationTargets("X Modulations", ModGravity.KEY_OUTPUTX, colorX)
            IndicatingModulationTargets("Y Modulations", ModGravity.KEY_OUTPUTY, colorY)
            IndicatingModulationTargets("Z Modulations", ModGravity.KEY_OUTPUTZ, colorZ)
        }
    }
}

@Composable
private fun IndicatingModulationTargets(title: String, paramKey: String, color: Color) {
    Box(modifier = Modifier
        .drawWithContent {
            drawContent()
            drawRect(color.copy(alpha = .2f))
        }
    ) {
        ModulationTargets(paramKey) {
            TargetsTitle(title, color)
        }
    }
}

@Composable
private fun TargetsTitle(
    title: String,
    indicatorColor: Color,
) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
        Indicator(indicatorColor, 20.dp)
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = title, textAlign = TextAlign.Center)
    }
}

@Composable
private fun rememberLiveState(
    editPluginViewModel: EditPluginViewmodel = viewModel(),
) : State<GravityLiveState> {
    return editPluginViewModel.liveEvents
        .filterIsInstance<GravityLiveState>()
        .collectAsState(initial = GravityLiveState())
}

@Composable
private fun Visualizer(
    modifier: Modifier = Modifier,
    editPluginViewModel: EditPluginViewmodel = viewModel(),
) {
    val liveState by rememberLiveState(editPluginViewModel = editPluginViewModel)

    Row(modifier = modifier,
        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center) {
            LiveImage(liveState)
        }
        Box(modifier = Modifier.weight(1f)) {
            LiveValues(liveState)
        }
    }
}

@Composable
private fun LiveImage(
    liveState: GravityLiveState,
    modifier: Modifier = Modifier,
) {
    VisualizerBox(
        background = colorBackground,
        modifier = modifier.size(100.dp)
    ) {
        Spacer(modifier = Modifier
            .drawWithCache {
                val sx = size.width / 2
                val sy = size.height / 2
                val pathx = Path().apply {
                    moveTo(size.center.x, size.center.y)
                    lineTo(
                        size.center.x + liveState.x.toFloat() * sx,
                        size.center.y
                    )
                    close()
                }
                val pathy = Path().apply {
                    moveTo(size.center.x, size.center.y)
                    lineTo(
                        size.center.x,
                        size.center.y + liveState.y.toFloat() * sy
                    )
                    close()
                }
                val pathz = Path().apply {
                    moveTo(size.center.x, size.center.y)
                    lineTo(
                        size.center.x + liveState.z.toFloat() * sy,
                        size.center.y + liveState.z.toFloat() * sy
                    )
                    close()
                }
                onDrawBehind {
                    drawPath(pathx, colorX, style = Stroke(width = 15f))
                    drawPath(pathy, colorY, style = Stroke(width = 15f))
                    drawPath(pathz, colorZ, style = Stroke(width = 15f))
                    drawCircle(colorCenter, radius = 15f, center = size.center)
                }
            }
            .fillMaxSize())
    }
}

@Composable
private fun LiveValues(
    liveState: GravityLiveState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Livevalue("X = %.2f".format(liveState.x), colorX)
        Livevalue("Y = %.2f".format(liveState.y), colorY)
        Livevalue("Z = %.2f".format(liveState.z), colorZ)
    }
}

@Composable
private fun Livevalue(
    valueText: String,
    indicatorColor: Color,
    textColor: Color = Color.White,
    style: TextStyle = Theme.fonts.labelSmall
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Indicator(indicatorColor, style.fontSize.value.dp)
        Spacer(modifier = Modifier.size(4.dp))
        Text(color = textColor, text = valueText, style = style)
    }
}

@Composable
private fun Indicator(
    color: Color,
    size: Dp,
) {
    Icon(modifier = Modifier.size(size),
        imageVector = Icons.Filled.Circle,
        tint = color,
        contentDescription = null)
}
