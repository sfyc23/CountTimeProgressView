[![twitter](https://img.shields.io/badge/twitter-sfyc23-blue.svg)](https://twitter.com/sfyc23)
[![微博](https://img.shields.io/badge/%E5%BE%AE%E5%8D%9A-sfyc23-blue.svg)](https://weibo.com/sfyc23)
[![API](https://img.shields.io/badge/API-%2B14-green.svg)](https://android-arsenal.com/api?level=14)
[![Maven Central](https://img.shields.io/badge/Maven%20Central-1.1.3-green.svg)]()
[![License](https://img.shields.io/badge/License-Apache%202.0-red.svg)]()
# CountTimeProgressView

**CountTimeProgressView** - 一个用 Kotlin 实现的有倒计时功能的，并能显现进度的 Android 库。


## Sample
![页面动图][1]


## 用法

**关于这个项目的实现功能，你可以通过 lib 包下的 [CountTimeProgressView][2] 查看实现**

### step 1

你可以将该库作为本地库项目加入，或者在 build.gradle 中添加依赖项。

```groovy
dependencies {
    compile 'com.sfyc.ctpv:library:1.1.3'
}
```

### Step 2

在你的 layout 中添加 CountTimeProgressView 控件，也可以添加自定义nttInclude the CountTimeProgressView widget in your layout. And you can customize it like this.

```xml
<com.sfyc.ctpv.CountTimeProgressView
    android:id="@+id/countTimeProgressView"
    android:layout_width="84dp"
    android:layout_height="84dp"
    android:layout_alignParentRight="true"
    app:backgroundColorCenter="#FF7F00"
    app:borderWidth="3dp"
    app:borderBottomColor="#D60000"
    app:borderDrawColor="#CDC8EA"
    app:markBallColor="#002FFF"
    app:markBallFlag="true"
    app:markBallWidth="3dp"
    app:titleCenterColor="#000000"
    app:titleCenterText="点击跳过"
    app:titleCenterSize="14sp"
    app:countTime="5000"
    app:startAngle="0"
    app:textStyle="jump"
    app:clockwise="true"
    />
```
### Step 3

你可以在代码中改变 CountTimeProgress 属性，并添加回调 『addOnEndListener』。例如：

by Java:
```java
countTimeProgressView.setBackgroundColorCenter(Color.WHITE);
countTimeProgressView.setBorderWidth(3);
countTimeProgressView.setBorderBottomColor(Color.GRAY);
countTimeProgressView.setBorderDrawColor(Color.RED);
countTimeProgressView.setMarkBallColor(Color.GREEN);

countTimeProgressView.setMarkBallFlag(true);
countTimeProgressView.setMarkBallWidth(4);
countTimeProgressView.setTitleCenterText("");
countTimeProgressView.setTitleCenterTextSize(16);
countTimeProgressView.setTitleCenterTextColor(Color.BLACK);

countTimeProgressView.setCountTime(5000L);
countTimeProgressView.setStartAngle(0);
countTimeProgressView.setTextStyle(CountTimeProgressView.TextStyle.INSTANCE.getCLOCK());
countTimeProgressView.setClockwise(true);

countTimeProgressView.addOnEndListener(new CountTimeProgressView.OnEndListener() {
    @Override
    public void onAnimationEnd() {
        Toast.makeText(SimpleActivity.this, "时间到", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onClick(long overageTime) {
        if (countTimeProgressView.isRunning()) {
            countTimeProgressView.cancelCountTimeAnimation();
        } else {
            countTimeProgressView.startCountTimeAnimation();
        }
    }
});

countTimeProgressView.startCountTimeAnimation();
```

by Kotlin:
```
with(countTimeProgressView){
    startAngle = 0f
    countTime = 6000L
    textStyle = CountTimeProgressView.TextStyle.SECOND
    borderWidth = 4f
    borderBottomColor = Color.GRAY
    borderDrawColor = Color.RED
    backgroundColorCenter = Color.WHITE
    markBallFlag = true
    markBallWidth = 6f
    markBallColor = Color.GREEN
    titleCenterText = "跳过（%s）s"
    titleCenterTextColor = Color.BLACK
    titleCenterTextSize = 16f
    addOnEndListener(object : CountTimeProgressView.OnEndListener {
        override fun onAnimationEnd() {
            Toast.makeText(this@SimpleActivity, "时间到", Toast.LENGTH_SHORT).show()
        }

        override fun onClick(overageTime: Long) {
            if (countTimeProgressView.isRunning) {
                countTimeProgressView.cancelCountTimeAnimation()
                Log.e("overageTime", "overageTime = " + overageTime)
            } else {
                countTimeProgressView.startCountTimeAnimation()
            }
        }
    })
}
countTimeProgressView.startCountTimeAnimation()
```

## 自定义属性说明

可自由灵活定制 :)

| 名称 | 格式 | 说明 |
| :---: | :---: | :---: |
| backgroundColorCenter | color | 圆环背景颜色，默认为 #00BCD4
| borderWidth | dimension | 圆形轨迹边框宽度, 默认为 3dp
| borderDrawColor | color | Draw color，默认为 #4dd0e1
| borderBottomColor | color | Bottom Color，默认为 #D32F2F
| markBallWidth | dimension | Ball width , 默认为 6dp
| markBallColor | color | Ball Color , 默认为 #536DFE
| markBallFlag | boolean | Ball isDisplay，默认为 true
| startAngle | float | Ball start angle , 默认为 0f
| clockwise | boolean | Clockwise or Counter-clockwise , 默认为 true
| countTime | integer | Count time , 默认为 5
| textStyle | enum | 中心文字显示风格 , 默认为 『jump』，其他可选项为： 『second』，『clock』，『none』
| titleCenterText | string | 中心文字显示内容 , 仅仅 TextStyle 当『 jump 』显示|
| titleCenterColor | color | 中心文字显示内容 , 默认为 #FFFFFF.
| titleCenterSize | dimension | 中心文字字体大小 , 默认为 16sp.

## Change Log

1.1.3(2017-11-11)
RuBuild Code by Kotlin

1.1.1(2017-3-27)
update Stop running when exiting

1.1.0 (2017-2-7)
update anim colckWise

1.0.0 (2016-12-20)
First commit

## Demo
[Download][3]


## Thanks

Inspired by

[tangqi92][4] created by [WaveLoadingView][5]


  [1]: https://github.com/sfyc23/CountTimeProgressView/blob/master/screenshot/ctpv-video-to-gif.gif
  [2]: https://github.com/sfyc23/CountTimeProgressView/blob/master/library/src/main/java/com/sfyc/ctpv/CountTimeProgressView.kt
  [3]: http://fir.im/ctpv58
  [4]: https://github.com/tangqi92
  [5]: https://github.com/tangqi92/WaveLoadingView