# CountTimeProgressView

**CountTimeProgressView** - An Android library that provides a count time circular progress view effect.


## Sample
![页面截图][1]


  ## Usage

**For a working implementation of this project see the `app/` folder.**

### Step 1

Include the library as a local library project or add the dependency in your build.gradle.

```groovy
dependencies {
    compile 'com.sfyc.ctpv:1.0.0'
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
        app:borderWidth="1dp"
        app:markBallColor="#002FFF"
        app:markBallFlag="true"
        app:markBallWidth="2dp"
        app:titleCenterColor="#000000"
        app:titleCenterSize="8sp"
        app:countTime="5000"
        app:textStyle="second"
        app:titleCenter="跳过"
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
1.2.1 (2016-12-20)
First commit

## Demo
[Download][2]


## Thanks

Inspired by

[tangqi92][3] created by [WaveLoadingView][4]



  [1]: http://oihnadz1x.bkt.clouddn.com/CountTimeProgressView01.png
  [2]: https://github.com/sfyc23/CountTimeProgressView/download/app-debug.apk
  [3]: https://github.com/tangqi92
  [4]: https://github.com/tangqi92/WaveLoadingView