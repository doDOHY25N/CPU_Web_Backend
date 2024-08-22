package com.cpu.web.dto.board;

import com.cpu.web.entity.board.Bulletin;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulletinDTO {
    private String  title;
    private String content;
    private boolean anonymous;

    public BulletinDTO(){}


    // entity => dto
    public BulletinDTO(Bulletin bulletin) {
        this.title = bulletin.getTitle();
        this.content = bulletin.getContent();
        this.anonymous = bulletin.getIsAnonymous();
    }

    // dto => entity
    public Bulletin toBulletinEntity(){
        Bulletin bulletin = new Bulletin();
        bulletin.setTitle(title);
        bulletin.setContent(content);
        bulletin.setIsAnonymous(anonymous);
        return bulletin;
    }


}

