package com.aleena.spotifytools.entity;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;




@Entity
@Table(name = "USERS")
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UserProfile{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USER_ID", unique = true)
    private String userId;

    public UserProfile(){}

    public UserProfile(String id){
        this.userId = id;
    }
}
