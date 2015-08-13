#import <Cocoa/Cocoa.h>
#import "Communicator.h"

@interface LXCBAppDelegate : NSObject <NSApplicationDelegate,CommDelegate>

@property (assign) IBOutlet NSWindow *window;
@property (assign) IBOutlet NSTextView *textView;
@property (assign) IBOutlet NSButton *button;

- (IBAction)buttonDidPress:(id)sender;

@end
