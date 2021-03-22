package com.example.demo.service.impl;

import com.example.demo.model.entity.NewsEntity;
import com.example.demo.model.entity.UserEntity;
import com.example.demo.model.service.AddNewsServiceModel;
import com.example.demo.model.view.NewsVieModel;
import com.example.demo.repository.NewsRepository;
import com.example.demo.service.CloudinaryService;
import com.example.demo.service.NewsService;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {
    private final ModelMapper modelMapper;
    private final NewsRepository newsRepository;
    private final CloudinaryService cloudinaryService;

    public NewsServiceImpl(ModelMapper modelMapper, NewsRepository newsRepository, CloudinaryService cloudinaryService) {
        this.modelMapper = modelMapper;
        this.newsRepository = newsRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    @CacheEvict(value = "news", allEntries = true)
    public void addNews(AddNewsServiceModel addNewsServiceModel) throws IOException {
        NewsEntity newsEntity = this.modelMapper.map(addNewsServiceModel, NewsEntity.class);
        if (addNewsServiceModel.getImageUrl().isEmpty()){
            newsEntity.setImageUrl("https://res.cloudinary.com/elizabetak/image/upload/v1616422823/mmn2ueedpftlieb6plbs.jpg");
            this.newsRepository.save(newsEntity);
        }else {
            MultipartFile img = addNewsServiceModel.getImageUrl();
            String url = cloudinaryService.uploadImage(img);
            newsEntity.setImageUrl(url);
            this.newsRepository.save(newsEntity);
        }

    }

    @Override
    @Cacheable("news")
    public List<NewsVieModel> findAllNews() {
        List<NewsVieModel> newsVieModels = new ArrayList<>();
        this.newsRepository.findAll().forEach(n -> {
            newsVieModels.add(this.modelMapper.map(n,NewsVieModel.class));
        });
        return newsVieModels;
    }
}
