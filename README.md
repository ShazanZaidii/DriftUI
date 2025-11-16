Till now it supports SwiftUI like: 
1. H,V and ZStacks -Same syntax, but you can wrap these in DriftView{} to center align and start modifying as in SwiftUI
2. Paddings Example- Text("hello", Modifier.padding(leading = 20)
3. Background & Colour
4. Shapes, ClipShape
6. Frame
7. font modifier - To use this we need to remove default "import androidx.compose.material3.Text" and add "import com.example.driftui.Text" (if you dont modify)


Steps to Use DriftUI:
1. Create a new Module of type Android Library
2. Name it driftui
3. if already not created, create a new package named- "com.example.driftui" and there create a new Kotlin Class/File and paste the contents of Layout.kt from the repo
4. Satisfy build.gradle requirements.
5. And you are ready to go!

Project Structure:
![ss](https://github.com/user-attachments/assets/ce719ab5-02a8-407b-98e2-5cc479a90a9f)

Checkout Screenhot:
![Screenshot 2025-11-17 at 1 28 04 AM](https://github.com/user-attachments/assets/894b674d-a3e8-4b94-8a59-e4aa5ccbf8d4)
