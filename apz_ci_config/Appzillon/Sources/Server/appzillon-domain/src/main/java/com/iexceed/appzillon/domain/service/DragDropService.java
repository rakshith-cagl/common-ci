/**
 *
 */
package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;

/**
 * @author Ripu This service written by ripu to do curd operation related to
 *         runtime drag and drop service This service class pointing to APP
 *         META. All Repository of this service class will do CURD Operation
 *         with APP META.
 */
@Named(ServerConstants.SERVICE_DRAG_DROP)
@Transactional(ServerConstants.TRANSACTION_APPZILLON_APP_META)
public class DragDropService {


    public void createDragDrop(Message pMessage) {
        // TODO document why this method is empty

    }


    public void deleteDragDrop(Message pMessage) {
        // TODO document why this method is empty

    }


    public void searchDragDrop(Message pMessage) {
        // TODO document why this method is empty
    }


    public void updateDragDrop(Message pMessage) {
        // TODO document why this method is empty
    }


}
