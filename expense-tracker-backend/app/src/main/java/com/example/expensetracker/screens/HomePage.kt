package com.example.expensetracker.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.expensetracker.R
import com.example.expensetracker.l_green
import kotlinx.serialization.builtins.serializer

@Composable
fun HomePage(){
    Surface(modifier = Modifier.fillMaxSize()){
        ConstraintLayout(modifier=Modifier.fillMaxSize()){
            val(topBar,nameBar,cardBal,transactionList)=createRefs()
            Image(
                modifier = Modifier.constrainAs(topBar){
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                painter = painterResource(R.drawable.appuprec),
                contentDescription = null
            )
            Box(modifier=Modifier
                .fillMaxWidth()
                .padding(top=64.dp, start=16.dp,end=16.dp)
                .constrainAs(nameBar){
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }){
                Column(modifier=Modifier.align(Alignment.CenterStart)) {
                    Text("Good afternoon,", color = Color.White)
                    Spacer(modifier=Modifier.size(8.dp))
                    Text("Akshay Tej", fontSize = 20.sp,fontWeight = FontWeight.SemiBold,color= Color.White)
                }
                Icon(modifier=Modifier.align(Alignment.CenterEnd),
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null)
            }
            CardBal(modifier=Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .constrainAs(cardBal){
                top.linkTo(nameBar.bottom,margin=32.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
            })
            Column(modifier=Modifier
                .fillMaxWidth()
                .padding(start=8.dp,end=8.dp)
                .constrainAs(transactionList){
                    top.linkTo(cardBal.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            ){
                Box(modifier=Modifier.fillMaxWidth()){
                    Text(modifier=Modifier.align(Alignment.CenterStart),text="Transactions History")
                    TextButton(onClick = {},modifier=Modifier.align(Alignment.CenterEnd)){
                        Text("See all")
                    }
                }
                TransactionList(modifier=Modifier)
            }
        }
    }
}


@Composable
fun CardBal(modifier:Modifier=Modifier){
    Column(modifier=modifier
        .shadow(16.dp,RoundedCornerShape(16.dp))
        .background(
            color=l_green
        )
        .padding(16.dp)
    )
    {
        Box(modifier=Modifier.fillMaxWidth()
        ){
            Column(modifier=Modifier.align(Alignment.TopStart)){
                Text("Total Balance",color=Color.White)
                Spacer(modifier=Modifier.size(4.dp))
                Text("$5000.00",color=Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
            Icon(modifier=Modifier.align(Alignment.TopEnd),
                imageVector = Icons.Default.KeyboardArrowUp, contentDescription = null)
        }
        Spacer(modifier=Modifier.height(32.dp))
        Row(modifier=Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            CardItem(Icons.Default.KeyboardArrowDown,"Income",7000.00)
            CardItem(Icons.Default.KeyboardArrowUp,"Expenses",2000.00)
        }
    }
}

@Composable
fun CardItem(icon: ImageVector,textStr:String,textVal: Double){
    Column(modifier=Modifier, horizontalAlignment = Alignment.CenterHorizontally){
        Row{
            Icon(imageVector = icon,contentDescription = null)
            Spacer(modifier=Modifier.width(8.dp))
            Text(textStr,color=Color.White)
        }
        Text("$textVal",color=Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun TransactionList(modifier:Modifier=Modifier){

}
@Composable
@Preview(showBackground = true)
fun HomePagePreview(){
    HomePage()
}