[![twitter](https://img.shields.io/badge/twitter-sfyc23-blue.svg)](https://twitter.com/sfyc23)
[![微博](https://img.shields.io/badge/%E5%BE%AE%E5%8D%9A-sfyc23-blue.svg)](https://weibo.com/sfyc23)
[![API](https://img.shields.io/badge/API-%2B14-green.svg)](https://android-arsenal.com/api?level=14)
[![Maven Central](https://img.shields.io/badge/Maven%20Central-1.1.3-green.svg)]()
[![License](https://img.shields.io/badge/License-Apache%202.0-red.svg)]()
# CountTimeProgressView

**CountTimeProgressView** - An Android library that provides a count time circular progress view effect.

[中文版][1]

## Sample
![页面动图][2]


## Usage

**For a working implementation of this project see the [CountTimeProgressView.kt][3] .**



### step 1

Include the library as a local library project or add the dependency in your build.gradle.

```groovy
dependencies {
    compile 'com.sfyc.ctpv:library:1.1.3'
}
```

### Step 2

Include the CountTimeProgressView widget in your layout. And you can customize it like this.

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

You can write some animation codes to the callbacks such as addOnEndListener, etc in your Activity.
in Java:
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

in Kotlin:
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
    titleCenterText = "jump（%s）s"
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

## Customization

Please feel free to :)

|name|format|description|
|:---:|:---:|:---:|
| backgroundColorCenter | color | Background_Color, default is #00BCD4
| borderWidth | dimension | Circular locus border width, default is 3dp
| borderDrawColor | color | Draw color，dafault is #4dd0e1
| borderBottomColor | color | Bottom Color，dafault is #D32F2F
| markBallWidth | dimension | Ball width , default is 6dp
| markBallColor | color | Ball Color , default is #536DFE
| markBallFlag | boolean | Ball isDisplay，default is true
| startAngle | float | Ball start angle , default is 0f
| clockwise | boolean | Clockwise or Counter-clockwise , default is true
| countTime | integer | Count time , default is 0
| textStyle | enum | Center text style , default is jump，Other options are available "second，clock，none"
| titleCenterText | string | Center text  , displayed only when textStyle is "jump"
| titleCenterColor | color | Center text color , default is #FFFFFF.
| titleCenterSize | dimension | Center text size , default is 16sp.

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
[Download][4]


## Thanks

Inspired by

[tangqi92][5] created by [WaveLoadingView][6]


  [1]: https://github.com/sfyc23/CountTimeProgressView/blob/master/README-ZH.md
  [2]: https://github.com/sfyc23/CountTimeProgressView/blob/master/screenshot/ctpv-video-to-gif.gif
  [3]: https://github.com/sfyc23/CountTimeProgressView/blob/master/library/src/main/java/com/sfyc/ctpv/CountTimeProgressView.kt
  [4]: http://fir.im/ctpv58
  [5]: https://github.com/tangqi92
  [6]: https://github.com/tangqi92/WaveLoadingView