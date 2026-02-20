import {fetchPost} from "@/net/index.js";

export const apiChatWithAI= async (context,onMessage,onError,onComplete)=>{
    try{
        const response = await fetchPost("/api/ai/chat",context)
        if(!response.ok){
            throw new Error(`HTTP error! status: ${response.status}`)
        }
        const reader = response.body.getReader()
        const decoder = new TextDecoder()
        while (true){
            const {done, value} = await reader.read()
            if(done) break
            onMessage(decoder.decode(value)
                .replaceAll("data:","")
                    .replaceAll("\n","")
                )
        }
        onComplete()
    }catch (e) {
        console.error('AI Chat Error:', e)
        onError(e)
    }
}