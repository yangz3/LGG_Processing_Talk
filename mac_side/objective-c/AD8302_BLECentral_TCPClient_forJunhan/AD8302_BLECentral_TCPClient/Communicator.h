#import <Foundation/Foundation.h>

@protocol CommDelegate;

@interface Communicator : NSObject <NSStreamDelegate> {
	@public
	
	NSString *host;
	int port;
}

@property (nonatomic, weak) id<CommDelegate> delegate;

- (void)setup;
- (void)open;
- (void)close;
- (void)stream:(NSStream *)stream handleEvent:(NSStreamEvent)event;
- (void)readIn:(NSString *)s;
- (void)writeOut:(NSString *)s;

@end


@protocol CommDelegate <NSObject>

-(void)Communicator:(Communicator*)Comm
receivedMessage:(NSString*)message;

@end