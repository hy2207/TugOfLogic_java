package com.example.tugoflogic_java;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DB_GameSetting {
    public int votingTime = 0;
    public boolean endGame = false;
    public boolean goNextBout = false;
    public boolean showList = false;
    public boolean showComment = false;
    public boolean startGame = false;
    public boolean isReassign = false;
    public boolean voteGround = false;
    public boolean goMain = false;

    public DB_GameSetting(int votingTime, boolean endGame, boolean goNextBout, boolean showList, boolean showComment, boolean startGame, boolean isReassign, boolean voteGround, boolean goMain) {
        this.votingTime = votingTime;
        this.endGame = endGame;
        this.goNextBout = goNextBout;
        this.showList = showList;
        this.showComment = showComment;
        this.startGame = startGame;
        this.isReassign = isReassign;
        this.voteGround = voteGround;
        this.goMain = goMain;
    }

    public DB_GameSetting(int votingTime, boolean endGame, boolean goNextBout, boolean showList, boolean showComment, boolean startGame, boolean isReassign, boolean voteGround) {
        this.votingTime = votingTime;
        this.endGame = endGame;
        this.goNextBout = goNextBout;
        this.showList = showList;
        this.showComment = showComment;
        this.startGame = startGame;
        this.isReassign = isReassign;
        this.voteGround = voteGround;
    }

    public DB_GameSetting(){

    }

}
