# story_teller

This project is based on Flutter and has achieved the Android section. You can communicate with a fine-tuned llm model, namely the Qwen_v2.5_7b_Instruct model of IFlytek Spark, and it can tell you a fairy tale story in both text and voice mode. As my expense is limited, I only use the free tts(text to voice) service version of the Volcano Large Model, in which case the voice could sound stiff. This model is trained in the English dataset, thus you can talk with it in Chinese and English, but other languages are not fundamentally supported.

## Prerequisite
1. An account of the IFlytek MAAS platform, in which case you can fine-tune a model based on various published and mature models:    
https://training.xfyun.cn/model/detail/2434949730866176?baseModelInfo=all&useDatasetId=all&listPageSize=6&listPage=1  
![image](https://github.com/user-attachments/assets/ddd189c0-9ab2-46c1-b9d4-68acf02111f7)  
2. An account of IFlytek Open Platform. It is a console where you will create an application, thus you can contribute your fine-tuned model to the application and experience your achievements:    
https://console.xfyun.cn/services/sparkapiCenter  
![image](https://github.com/user-attachments/assets/4716c16e-c411-4379-a522-908bc96bad6e)  
**Please use the parameters in those two sections to fulfill parameters in this file:https://github.com/helloKittyCatSweet/StoryTeller/blob/main/android/app/src/main/java/com/example/story_teller/BigModelNew.java where is "xxx"**  

3. An account of Volcano Large Model. You can create a trial account if you just want to try, and you'll get 20,000 API quotas.:  
https://console.volcengine.com/speech/service/8?AppID=1570971706
![image](https://github.com/user-attachments/assets/e81fb052-a4c2-4ba4-a1ae-f504921ac018)
**Please use the parameters in the sections to fulfill parameters in files:(https://github.com/helloKittyCatSweet/StoryTeller/blob/main/android/app/src/main/java/com/example/story_teller/TtsRequest.java) and https://github.com/helloKittyCatSweet/StoryTeller/blob/main/android/app/src/main/java/com/example/story_teller/TtsWebsocket.java where is "xxx"**  

## Getting Started

This project is a starting point for a Flutter application.

A few resources to get you started if this is your first Flutter project:

- [Lab: Write your first Flutter app](https://docs.flutter.dev/get-started/codelab)
- [Cookbook: Useful Flutter samples](https://docs.flutter.dev/cookbook)

For help getting started with Flutter development, view the
[online documentation](https://docs.flutter.dev/), which offers tutorials,
samples, guidance on mobile development, and a full API reference.

You can simply test it on Nox adb.

For details about dataset creation, you can check the "Python code" folder yourself. If any questions, you can check CSDN first, or communicate with me whether under my CSDN blog or this repository. I'm really glad to chat with you.
=======

## Demo presentation
https://github.com/helloKittyCatSweet/StoryTeller/blob/main/videos/%E7%AB%A5%E8%AF%9D%E6%95%85%E4%BA%8B%E5%AE%B6app.mp4

## Other information to refer to
base trial:  
https://blog.csdn.net/qq_61897993/article/details/144477098?sharetype=blogdetail&sharerId=144477098&sharerefer=PC&sharesource=qq_61897993&spm=1011.2480.3001.8118
advanced trial:  
https://blog.csdn.net/qq_61897993/article/details/144635398

## Permission and Authorization
This project is open source for anyone to use, no matter for an enterprise or an individual.
