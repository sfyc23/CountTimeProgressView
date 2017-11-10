[![twitter](https://img.shields.io/badge/twitter-sfyc23-blue.svg)](https://twitter.com/sfyc23)
[![微博](https://img.shields.io/badge/%E5%BE%AE%E5%8D%9A-sfyc23-blue.svg)](https://weibo.com/sfyc23)
[![API](https://img.shields.io/badge/API-8%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=8)
[![Maven Central](https://img.shields.io/badge/Maven%20Central-1.1.3-green.svg)]()
[![License](https://img.shields.io/badge/License-Apache%202.0-red.svg)]()
# CountTimeProgressView

**CountTimeProgressView** - An Android library that provides a count time circular progress view effect.


## Sample
![页面动图][1]


  ## Usage

**For a working implementation of this project see the `app/` folder.**

###  1

Include the library as a local library project or add the dependency in your build.gradle.

```groovy
dependencies {
    compile 'com.sfyc.ctpv:library:1.1.1'
}
```

### Step 2

Include the CountTimeProgressView widget in your layout. And you can customize it like this.

```xml
    <com.sfyc.ctpv.CountTimeProgressView
        android:id="@+id/countTimeProgressView"
        android:layout_marginTop="4dp"
        android:layout_marginLeft="4dp"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_alignParentRight="true"
        app:background_Color="#FF7F00"
        app:borderWidth="3dp"
        app:borderBottomColor="#D60000"
        app:borderDrawColor="#CDC8EA"
        app:markBallColor="#002FFF"
        app:markBallFlag="true"
        app:markBallWidth="3dp"
        app:titleCenterColor="#000000"
        app:titleCenter="跳过"
        app:titleCenterSize="14sp"
        app:countTime="5000"
        app:textStyle="jump"
        app:clockwise="true"
        />
```
### Step 3

You can write some animation codes to the callbacks such as setOnCheckedChangeListener, onProgressChanged, etc in your Activity.
```java
    countTimeProgressView.setStartAngle(0);
    countTimeProgressView.setCountTime(6000);
    countTimeProgressView.setTextStyle(CountTimeProgressView.TextStyle.SECOND);
    countTimeProgressView.setBorderWidth(6);
    countTimeProgressView.setBorderBottomColor(Color.GRAY);
    countTimeProgressView.setBorderDrawColor(Color.RED);
    countTimeProgressView.setBackgroundColor(Color.WHITE);
    countTimeProgressView.setMarkBallFlag(true);
    countTimeProgressView.setMarkBallWidth(4);
    countTimeProgressView.setMarkBallColor(Color.GREEN);
    countTimeProgressView.setTitleCenter("跳过（%s）s");
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
                Log.e("overageTime","overageTime = "+overageTime);
            } else {
                countTimeProgressView.startCountTimeAnimation();
            }
        }
    });

    countTimeProgressView.startCountTimeAnimation();

```

## Customization

Please feel free to :)

|name|format|description|
|:---:|:---:|:---:|
| background_Color | color | Background_Color, default is white
| borderWidth | dimension | Circular locus border width, default is 3dp
| borderDrawColor | color | Draw color
| borderBottomColor | color | Bottom Color
| markBallWidth | dimension | Ball width , default is 3dp
| markBallColor | color | Ball Color , default is red
| markBallFlag | boolean | Bottom Color
| startAngle | integer | Ball start angle , default is 0
| clockwise | boolean | Clockwise or Counter-clockwise , default is true
| countTime | integer | Count time , default is 0
| textStyle | enum | Center text style , default is jump
| titleCenter | string | Center text style , displayed only when textStyle is "jump"
| titleCenterColor | color | Center text style , default is black.
| titleCenterSize | dimension | Center text size , default is 16sp.

## Change Log

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
  [3]: http://fir.im/ctpv58
  [4]: https://github.com/tangqi92
  [5]: https://github.com/tangqi92/WaveLoadingView