package org.dodgybits.shuffle.android.synchronisation.tracks.parsing;

import android.text.TextUtils;
import com.google.common.collect.Lists;
import org.dodgybits.shuffle.android.core.activity.flurry.Analytics;
import org.dodgybits.shuffle.android.core.model.EntityBuilder;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.Task.Builder;
import org.dodgybits.shuffle.android.core.util.DateUtils;
import roboguice.util.Ln;

import java.text.ParseException;
import java.util.List;

public class TaskParser extends Parser<Task> {

	private Builder mTaskBuilder;
	protected IContextLookup mContextLookup;
	protected IProjectLookup mProjectLookup;

	public TaskParser(IContextLookup contextLookup, IProjectLookup projectLookup, Analytics analytics) {
		super("todo", analytics);
		mContextLookup = contextLookup;
		mProjectLookup = projectLookup;
		
		appliers.put("state", new Applier() {
			@Override
			public boolean apply(String value) {
                Ln.d("Got status %s", value);

				if( value.equals("completed")) {
                    mTaskBuilder.setComplete(true);
                }
				return true;
			}
		});
		
		appliers.put("description",
				new Applier(){
					@Override
					public boolean apply(String value) {

                        mTaskBuilder.setDescription(value);
						return true;
					}
			
		});
		appliers.put("notes",
				new Applier(){
					@Override
					public boolean apply(String value) {
						
						mTaskBuilder.setDetails(value);
						return true;
					}
			
		});
		appliers.put("id",
				new Applier(){
					@Override
					public boolean apply(String value) {
				        Id tracksId = Id.create(Long.parseLong(value));
				        mTaskBuilder.setTracksId(tracksId);
                        return true;
					}
			
		});
		appliers.put("updated-at",
				new Applier(){
					@Override
					public boolean apply(String value) {
						 
                         long date;
							try {
                                date = DateUtils.parseIso8601Date(value);
								mTaskBuilder.setModifiedDate(date);
							    
							    return true;
							} catch (ParseException e) {
								return false;
							}
					}
			
		});
		appliers.put("context-id",
				new Applier(){
					@Override
					public boolean apply(String value) {
                        if (!TextUtils.isEmpty(value)) {
                            Id tracksId = Id.create(Long.parseLong(value));
                            Id contextId = mContextLookup.findContextIdByTracksId(tracksId);
                            if (contextId.isInitialised()) {
                                List<Id> contextIds = Lists.newArrayList(contextId);
                                mTaskBuilder.setContextIds(contextIds);
                            }
                        }
                        return true;
					}
		});
		appliers.put("project-id",
				new Applier(){
					@Override
					public boolean apply(String value) {
                        if (!TextUtils.isEmpty(value)) {
                            Id tracksId = Id.create(Long.parseLong(value));
                            Id project = mProjectLookup.findProjectIdByTracksId(tracksId);
                            if (project.isInitialised()) {
                                mTaskBuilder.setProjectId(project);
                            }
                        }
                        return true;
					}
		});
		appliers.put("created-at",
				new Applier(){
					@Override
					public boolean apply(String value) {
						 
                         if (!TextUtils.isEmpty(value)) {
							try {
								long created = DateUtils.parseIso8601Date(value);
								mTaskBuilder.setCreatedDate(created);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								return false;
							}
                             
                         }
                        return true;
					}
		});
		appliers.put("due",
				new Applier(){
			@Override
			public boolean apply(String value) {

                if (!TextUtils.isEmpty(value)) {
					try {
						long due = DateUtils.parseIso8601Date(value);
						mTaskBuilder.setDueDate(due);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						return false;
					}
                     
                 }
                return true;
			}
});
		appliers.put("show-from",
				new Applier(){
					@Override
					public boolean apply(String value) {

                        if (!TextUtils.isEmpty(value)) {
							try {
								long showFrom = DateUtils.parseIso8601Date(value);
								mTaskBuilder.setStartDate(showFrom);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								return false;
							}
                             
                         }
                        return true;
					}
		});
	}

	@Override
	protected EntityBuilder<Task> createBuilder() {
		return mTaskBuilder = Task.newBuilder();
	}

}
