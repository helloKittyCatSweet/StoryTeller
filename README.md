# story_teller

This project is based on **Flutter** and has achieved the Android section. You can communicate with a fine-tuned llm model, namely the **Qwen_v2.5_7b_Instruct** model of IFlytek Spark, and it can tell you a fairy tale story in both text and voice mode. As my expense is limited, I only use the free tts(text to voice) service version of the Volcano Large Model, in which case the voice could sound stiff. This model is trained in the English dataset, thus you can talk with it in Chinese and English, but other languages are not fundamentally supported. The dataset I use is a slice of **TinyStories**.  
这个项目基于 **Flutter** 框架，并且已经完成了安卓部分的开发。您可以通过与一个精调的大型语言模型——即科大讯飞火花（IFlytek Spark）的 **Qwen_v2.5_7b_Instruct** 模型进行交互，它能够以文本和语音模式为您讲述童话故事。由于我的经费有限，我只使用了火山大模型的免费文本转语音（TTS）服务版本，在这种情况下，声音可能会显得有些生硬。这个模型是在英文数据集上训练的，因此您可以用中文和英文与它对话，但其他语言基本上不支持。我使用的数据集是 **TinyStories** 的一部分。

## Prerequisite
1. An account of the IFlytek MAAS platform, in which case you can fine-tune a model based on various published and mature models:  
需要一个科大讯飞MAAS（Mobile AI Assistant Service）平台的账户，您可以在该平台上基于各种已发布和成熟的模型进行模型的微调。      
https://training.xfyun.cn/model/detail/2434949730866176?baseModelInfo=all&useDatasetId=all&listPageSize=6&listPage=1  
![image](https://github.com/user-attachments/assets/ddd189c0-9ab2-46c1-b9d4-68acf02111f7)  
2. An account of IFlytek Open Platform. It is a console where you will create an application, thus you can contribute your fine-tuned model to the application and experience your achievements:  
需要一个科大讯飞开放平台的账户。这是一个控制台，您可以在其中创建应用程序，进而将您精调的模型贡献给该应用程序，并体验您的成果。       
https://console.xfyun.cn/services/sparkapiCenter  
![image](https://github.com/user-attachments/assets/4716c16e-c411-4379-a522-908bc96bad6e)  
**Please use the parameters in those two sections to fulfill parameters in this file:https://github.com/helloKittyCatSweet/StoryTeller/blob/main/android/app/src/main/java/com/example/story_teller/BigModelNew.java where is "xxx"**  

3. An account of Volcano Large Model. You can create a trial account if you just want to try, and you'll get 20,000 API quotas.  
一个火山大模型的账户。如果您只是想尝试使用，您可以创建一个试用账户，您将获得20,000次API调用配额。:  
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
您可以通过Nox ADB简单地测试它。  

For details about dataset creation, you can check the "Python code" folder yourself. If any questions, you can check CSDN first, or communicate with me whether under my CSDN blog or this repository. I'm really glad to chat with you.  
有关数据集创建的详细信息，您可以自己查看“Python code”文件夹。如果有任何问题，您可以先查看CSDN，或者通过我的CSDN博客或此仓库与我交流。我很高兴与您聊天。  

## Demo presentation
https://github.com/helloKittyCatSweet/StoryTeller/blob/main/videos/%E7%AB%A5%E8%AF%9D%E6%95%85%E4%BA%8B%E5%AE%B6app.mp4

## Other information to refer to
base trial:  
https://blog.csdn.net/qq_61897993/article/details/144477098?sharetype=blogdetail&sharerId=144477098&sharerefer=PC&sharesource=qq_61897993&spm=1011.2480.3001.8118  
advanced trial:  
https://blog.csdn.net/qq_61897993/article/details/144635398

## Permission and Authorization
This project is open source for anyone to use, no matter for an enterprise or an individual.  
这个项目是开源的，任何人都可以使用，无论是企业还是个人。
