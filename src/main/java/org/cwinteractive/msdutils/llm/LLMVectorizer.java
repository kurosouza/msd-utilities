package org.cwinteractive.msdutils.llm;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.OnnxEmbeddingModel;
import dev.langchain4j.model.embedding.onnx.PoolingMode;
import dev.langchain4j.model.output.Response;

public class LLMVectorizer {

    private String modelPath;
    private String tokenizerPath;
    private EmbeddingModel embeddingModel;

    public LLMVectorizer(String modelPath, String tokenizerPath) {
        this.modelPath = modelPath;
        this.tokenizerPath = tokenizerPath;

        initialize();
    }

    private void initialize() {
        PoolingMode poolingMode = PoolingMode.MEAN;
        embeddingModel = new OnnxEmbeddingModel(modelPath, tokenizerPath, poolingMode);
    }

    public Embedding getEmbedding(String text) {
        Response<Embedding> embeddingResponse = embeddingModel.embed(text);
        return embeddingResponse.content();
    }

    public static LLMVectorizer getInstance() {
        return new LLMVectorizer("/home/dreamchild/projects/hack/payara/models/jinaai-embeddings-v3/model.onnx",
                "/home/dreamchild/projects/hack/payara/models/jinaai-embeddings-v3/tokenizer.json");
    }

}
