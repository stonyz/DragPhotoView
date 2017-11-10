# DragPhotoView 高仿微信可拖拽返回PhotoView

forked from [githubwing/DragPhotoView](https://github.com/githubwing/DragPhotoView)，进行二次调整与优化

## 一、介绍

目前已经优化部分

* 共享动画效果（后续继续优化）
* 触摸，拖动，长按，滑动复原等问题优化
* 集成ViewPager模式，支持拖拽与不拖拽，方便直接调用大图查看

------------------------------------------------------------------

（后面再更新演示图）
![](https://github.com/githubwing/DragPhotoView/raw/master/img/img.gif)

## 二、依赖

### 在你的项目project下的build.gradle添加
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
### 在module下的build.gradle添加依赖

```
dependencies {
    compile 'com.github.CarGuo:DragPhotoView:1.0.2'
}

```

# License

    Copyright 2016 androidwing1992

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
